package com.arpit.collegetrade.data

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

interface AdRepository {

    suspend fun postAd(ad: Ad)

    suspend fun getNextBatch(
        adsTreeMap: TreeMap<String, Ad>,
        lastDocSnapshot: DocumentSnapshot?,
        userId: String
    ): HashMap<String, Any?>

    suspend fun getAdFromId(id: String): Ad?

    fun updateFavList(ad: Ad, userId: String, addToFav: Boolean)

    suspend fun addToViewersList(adId: String, userId: String)

    suspend fun getFavorites(userId: String): TreeMap<String, Ad>
}