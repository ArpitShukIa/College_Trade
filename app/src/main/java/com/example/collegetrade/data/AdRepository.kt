package com.example.collegetrade.data

import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

interface AdRepository {

    suspend fun postAd(ad: Ad)

    suspend fun getNextBatch(
        adsTreeMap: TreeMap<String, Ad>,
        lastDocSnapshot: DocumentSnapshot?
    ): HashMap<String, Any?>
}