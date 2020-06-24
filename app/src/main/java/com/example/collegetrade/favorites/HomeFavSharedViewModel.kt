package com.example.collegetrade.favorites

import android.util.Log
import androidx.lifecycle.*
import com.example.collegetrade.Application
import com.example.collegetrade.data.Ad
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class HomeFavSharedViewModel(application: Application) : ViewModel() {

    private val repository = application.repository
    private val userId = application.currentUserId

    private val adsTreeMap = MutableLiveData(TreeMap<String, Ad>())
    private val favTreeMap = MutableLiveData(TreeMap<String, Ad>())

    val ads: LiveData<List<Ad>> = adsTreeMap.map { getSortedMap(it) }
    val favAds: LiveData<List<Ad>> = favTreeMap.map { getSortedMap(it) }

    private var lastDocSnapshot: DocumentSnapshot? = null
    private var allAdsShown = false

    private val _refreshingHome = MutableLiveData<Boolean>()
    val refreshingHome: LiveData<Boolean> = _refreshingHome

    private val _refreshingFav = MutableLiveData<Boolean>()
    val refreshingFav: LiveData<Boolean> = _refreshingFav

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private suspend fun getAdsBatch() {
        if (allAdsShown) return

        _showProgressBar.value = lastDocSnapshot != null

        withContext(Dispatchers.Default) {
            val result = repository.getNextBatch(adsTreeMap.value!!, lastDocSnapshot)

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

    fun getAds() {
        viewModelScope.launch {
            if (!allAdsShown)
                getAdsBatch()
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favTreeMap.value = repository.getFavorites(userId)
            _refreshingFav.value = false
        }
    }

    fun updateFavList(ad: Ad, addToFav: Boolean) {
        val adsTreeMap = adsTreeMap.value!!
        val favTreeMap = favTreeMap.value!!
        val key = "${ad.timestamp}${ad.id}"

        if (adsTreeMap.containsKey(key))
            adsTreeMap[key] = ad

        if (addToFav) {
            if (ad.sellerId != userId)
                favTreeMap["1$key"] = ad
            else
                favTreeMap["2$key"] = ad
        } else {
            if (favTreeMap.containsKey("1$key"))
                favTreeMap.remove("1$key")
            else if (favTreeMap.containsKey("2$key"))
                favTreeMap.remove("2$key")
        }

        this.favTreeMap.value = favTreeMap
        this.adsTreeMap.value = adsTreeMap

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
        lastDocSnapshot = null
        allAdsShown = false

        viewModelScope.launch {
            adsTreeMap.value = TreeMap()
            getAdsBatch()
            _refreshingHome.value = false
        }
    }

    fun refreshFav() {
        _refreshingFav.value = true
        loadFavorites()
    }

}