package com.example.collegetrade.data

import androidx.core.net.toUri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

object DefaultAdRepository : AdRepository {

    private const val TAG = "TAG DefaultAdRepository"

    private val firestore: FirebaseFirestore by lazy { Firebase.firestore }
    private val storage: StorageReference by lazy { Firebase.storage.reference }

    override suspend fun postAd(ad: Ad) {
        coroutineScope {
            val doc1 = firestore.collection("Ads").document()
            val doc2 = firestore.collection("Users")
                .document(ad.sellerId).collection("My Ads").document(doc1.id)
            val imageRef = storage.child("Ad Images/${doc1.id}/main_image.jpeg")

            imageRef.putFile(ad.image.toUri()).await()
            val downloadUrl = imageRef.downloadUrl.await()

            ad.id = doc1.id
            ad.image = downloadUrl.toString()

            firestore.runBatch { batch ->
                batch.set(doc1, ad)
                batch.set(doc2, ad)
            }.await()
        }
    }
}