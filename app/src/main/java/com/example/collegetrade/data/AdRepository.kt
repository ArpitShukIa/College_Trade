package com.example.collegetrade.data

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

interface AdRepository {

    suspend fun postAd(ad: Ad)

    suspend fun getNextBatch(
        adsTreeMap: TreeMap<String, Ad>,
        lastDocSnapshot: DocumentSnapshot?
    ): HashMap<String, Any?>

    suspend fun getAdFromId(id: String): Ad?

    suspend fun getLikesCount(adId: String): Int
    suspend fun getViewsCount(adId: String): Int
    suspend fun isLiked(adId: String, uid: String): Boolean

    fun updateFavList(ad: Ad, userId: String, addToFav: Boolean)

    fun addToViewersList(adId: String, userId: String)
}