package com.arpit.collegetrade.favorites

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.*
import com.arpit.collegetrade.Application
import com.arpit.collegetrade.Event
import com.arpit.collegetrade.data.Ad
import com.arpit.collegetrade.util.getUserName
import com.google.firebase.firestore.DocumentSnapshot
import io.tempo.Tempo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class SharedViewModel(private val application: Application) : ViewModel() {

    private val repository = application.adRepository
    private val userId = application.currentUser.id

    val currentTime = liveData(viewModelScope.coroutineContext + Dispatchers.Default) {
        while (true) {
            val time = Tempo.now() ?: System.currentTimeMillis()
            emit(time)
            delay(1000)
        }
    }

    private val adsTreeMap = MutableLiveData(TreeMap<String, Ad>())
    private val favTreeMap = MutableLiveData(TreeMap<String, Ad>())
    private val myAdsTreeMap = MutableLiveData(TreeMap<String, Ad>())

    val ads: LiveData<List<Ad>> = adsTreeMap.map { getSortedMap(it) }
    val favAds: LiveData<List<Ad>> = favTreeMap.map { getSortedMap(it) }
    val myAds: LiveData<List<Ad>> = myAdsTreeMap.map { getSortedMap(it) }

    private var lastDocSnapshot: DocumentSnapshot? = null
    private var allAdsShown = false

    private val _refreshingHome = MutableLiveData<Boolean>()
    val refreshingHome: LiveData<Boolean> = _refreshingHome

    private val _refreshingFav = MutableLiveData<Boolean>()
    val refreshingFav: LiveData<Boolean> = _refreshingFav

    private val _refreshingMyAds = MutableLiveData<Boolean>()
    val refreshingMyAds: LiveData<Boolean> = _refreshingMyAds

    private val _scrollToTop = MutableLiveData(Event(false))
    val scrollToTop: LiveData<Event<Boolean>> = _scrollToTop

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private val _userRetrieved = MutableLiveData<Event<Boolean>>()
    val userRetrieved: LiveData<Event<Boolean>> = _userRetrieved

    var isDeepLinkHandled = false
    var firstTimeRefresh = true

    var stateHome: Parcelable? = null

    fun getUser() {
        viewModelScope.launch {
            getUserFromDatabase()
        }
    }

    private suspend fun getUserFromDatabase() {
        try {
            val user = repository.getUser()
            user.name = getUserName(user.name)
            if (user.contactNumber == null)
                user.contactNumber = ""
            application.currentUser = user
            _userRetrieved.postValue(Event(true))
        } catch (e: Exception) {
            Log.e("TAG", "getUserFromDatabase: ${e.stackTrace}", e)
            _userRetrieved.postValue(Event(false))
        }
    }

    fun getAds() {
        viewModelScope.launch {
            if (!allAdsShown)
                getAdsBatch()
        }
    }

    private suspend fun getAdsBatch() {
        if (allAdsShown) return

        _showProgressBar.value = lastDocSnapshot != null

        withContext(Dispatchers.Default) {
            val result = repository.getNextBatch(adsTreeMap.value!!, lastDocSnapshot, userId)

            adsTreeMap.postValue(result["ads"] as TreeMap<String, Ad>)

            _showProgressBar.postValue(false)

            (result["lastDoc"] as DocumentSnapshot?).also {
                if (it == lastDocSnapshot)
                    allAdsShown = true
                else
                    lastDocSnapshot = it
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favTreeMap.value = repository.getAds(userId, "Favourites")
            _refreshingFav.value = false
        }
    }

    private fun loadMyAds() {
        viewModelScope.launch {
            myAdsTreeMap.value = repository.getAds(userId, "My Ads")
            _refreshingMyAds.value = false
        }
    }

    fun updateFavList(ad: Ad, addToFav: Boolean) {

        val adsTreeMap = adsTreeMap.value!!
        val favTreeMap = favTreeMap.value!!
        val myAdsTreeMap = myAdsTreeMap.value!!

        val key = "${ad.timestamp}${ad.id}"
        val favKey = "${ad.likeTime}${ad.id}"

        if (adsTreeMap.containsKey(key))
            adsTreeMap[key] = ad

        if (myAdsTreeMap.containsKey(key))
            myAdsTreeMap[key] = ad

        if (addToFav)
            favTreeMap[favKey] = ad
        else
            favTreeMap.remove(favKey)

        this.adsTreeMap.value = adsTreeMap
        this.myAdsTreeMap.value = myAdsTreeMap
        this.favTreeMap.value = favTreeMap

        try {
            repository.updateFavList(ad, userId, addToFav)
        } catch (e: Exception) {
            Log.e("TAG", "updateFavList: ${e.stackTrace}", e)
        }
    }

    private fun getSortedMap(map: TreeMap<String, Ad>): List<Ad> {
        val sortedMap = TreeMap<String, Ad>(Collections.reverseOrder())
        sortedMap.putAll(map)
        return ArrayList(sortedMap.values)
    }

    fun refreshHome() {
        _refreshingHome.value = true
        _scrollToTop.value = Event(false)
        lastDocSnapshot = null
        allAdsShown = false

        viewModelScope.launch {
            adsTreeMap.value = TreeMap()
            getAdsBatch()
            _refreshingHome.value = false
            if (adsTreeMap.value?.size in 1..8) // TODO Update the first batch size
                _scrollToTop.value = Event(true)
        }
    }

    fun refreshFav() {
        _refreshingFav.value = true
        loadFavorites()
    }

    fun refreshMyAds() {
        _refreshingMyAds.value = true
        loadMyAds()
    }

}