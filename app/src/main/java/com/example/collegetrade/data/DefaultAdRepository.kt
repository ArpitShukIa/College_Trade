package com.example.collegetrade.data

import androidx.core.net.toUri
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.HashMap

object DefaultAdRepository : AdRepository {

    private const val TAG = "TAG DefaultAdRepository"

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val storage: StorageReference by lazy { Firebase.storage.reference }

    override suspend fun postAd(ad: Ad) {

        val adsCollection = firestore.collection("Ads")
        val doc1 = if (ad.id == "") adsCollection.document() else adsCollection.document(ad.id)
        val doc2 = firestore.collection("Users")
            .document(ad.sellerId).collection("My Ads").document(doc1.id)
        val imageRef = storage.child("Ad Images/${doc1.id}/main_image.jpeg")

        if (!ad.image.startsWith("https")) {
            imageRef.putFile(ad.image.toUri()).await()
            val downloadUrl = imageRef.downloadUrl.await()
            ad.image = downloadUrl.toString()
        }

        ad.id = doc1.id

        firestore.runBatch { batch ->
            batch.set(doc1, ad)
            batch.set(doc2, ad)
        }.await()
    }

    override suspend fun getNextBatch(
        adsTreeMap: TreeMap<String, Ad>,
        lastDocSnapshot: DocumentSnapshot?
    ): HashMap<String, Any?> {

        var query = firestore.collection("Ads")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        query = if (lastDocSnapshot == null)
            query.limit(8)
        else
            query.startAfter(lastDocSnapshot).limit(4)

        val documentSnapshots = query.get().await()
        val documentsCount = documentSnapshots.documents.size
        var lastVisible = lastDocSnapshot

        if (documentsCount > 0) {

            for (doc in documentSnapshots) {
                val timestamp = doc.get("timestamp").toString()
                val adId = doc.get("id").toString()
                val key = timestamp + adId
                adsTreeMap[key] = doc.toObject(Ad::class.java)
            }

            lastVisible = documentSnapshots.documents[documentsCount - 1]
        }

        val hashMap = HashMap<String, Any?>()
        hashMap["ads"] = adsTreeMap
        hashMap["lastDoc"] = lastVisible
        return hashMap
    }

    override suspend fun getAdFromId(id: String): Ad? {
        return try {
            firestore.collection("Ads").document(id).get()
                .await()
                .toObject(Ad::class.java)
        } catch (e: Exception) {
            null
        }
    }
}