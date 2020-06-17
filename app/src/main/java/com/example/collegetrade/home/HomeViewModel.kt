package com.example.collegetrade.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.collegetrade.Application
import com.example.collegetrade.data.Ad
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
class HomeViewModel(application: Application) : ViewModel() {

    private val repository = application.repository

    private val _ads = MutableLiveData<List<Ad>>()
    val ads: LiveData<List<Ad>> = _ads

    private var adsTreeMap = TreeMap<String, Ad>(Collections.reverseOrder())
    private var lastDocSnapshot: DocumentSnapshot? = null
    private var allAdsShown = false

    private val _refreshing = MutableLiveData<Boolean>()
    val refreshing: LiveData<Boolean> = _refreshing

    private val _showProgressBar = MutableLiveData(false)
    val showProgressBar: LiveData<Boolean> = _showProgressBar

    private suspend fun getAdsBatch() {
        if (allAdsShown) return

        _showProgressBar.value = lastDocSnapshot != null

        withContext(Dispatchers.Default) {
            val result = repository.getNextBatch(adsTreeMap, lastDocSnapshot)

            val adsTreeMap = result["ads"] as TreeMap<String, Ad>
            _ads.postValue(ArrayList(adsTreeMap.values))

            _showProgressBar.postValue(false)

            (result["lastDoc"] as DocumentSnapshot).also {
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

    fun refresh() {
        _refreshing.value = true
        lastDocSnapshot = null
        allAdsShown = false

        viewModelScope.launch {
            adsTreeMap.clear()
            getAdsBatch()
            _refreshing.value = false
        }
    }
}