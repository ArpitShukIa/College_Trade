@file:Suppress("UNCHECKED_CAST")

package com.arpit.collegetrade.data

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
        val doc2 = firestore.collection("Users").document(ad.sellerId)
            .collection("My Ads").document(doc1.id)
        val imageRef = storage.child("Ad Images/${doc1.id}/main_image.jpeg")

        if (!ad.image.startsWith("https")) {
            imageRef.putFile(ad.image.toUri()).await()
            val downloadUrl = imageRef.downloadUrl.await()
            ad.image = downloadUrl.toString()
        }

        ad.id = doc1.id

        firestore.runBatch { batch ->
            batch.set(doc1, ad)
            batch.set(doc2, hashMapOf("timestamp" to ad.timestamp, "sellerId" to ad.sellerId))
        }.await()
    }

    override suspend fun getNextBatch(
        adsTreeMap: TreeMap<String, Ad>,
        lastDocSnapshot: DocumentSnapshot?,
        userId: String
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
                val ad = doc.toObject(Ad::class.java)
                val key = ad.timestamp.toString() + ad.id
                ad.likesCount = ad.likers.size
                ad.isLiked = ad.likers.contains(userId)
                adsTreeMap[key] = ad
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

    override fun updateFavList(ad: Ad, userId: String, addToFav: Boolean) {

        val favDoc = firestore.collection("Users").document(userId)
            .collection("Favourites").document(ad.id)
        val adDoc = firestore.collection("Ads").document(ad.id)

        firestore.runTransaction { transaction ->
            if (addToFav) {
                val snapshot = transaction.get(adDoc)
                val likers = snapshot.get("likers") as ArrayList<String>
                likers.add(userId)
                transaction.update(adDoc, "likers", likers)

                transaction.set(
                    favDoc, hashMapOf("timestamp" to ad.timestamp, "sellerId" to ad.sellerId)
                )
            } else {
                val snapshot = transaction.get(adDoc)
                val likers = snapshot.get("likers") as ArrayList<String>
                likers.remove(userId)
                transaction.update(adDoc, "likers", likers)
                transaction.delete(favDoc)
            }
        }
    }

    override suspend fun addToViewersList(adId: String, userId: String) {
        val adDocRef = firestore.collection("Ads").document(adId)
        adDocRef.collection("Viewed By").document(userId).set(hashMapOf("exists" to true)).await()
        val views = adDocRef.collection("Viewed By").get().await().documents.size
        adDocRef.update("viewsCount", views)
    }

    override suspend fun getAds(userId: String, location: String): TreeMap<String, Ad> {

        val docsSnapshot = firestore.collection("Users").document(userId)
            .collection(location).get().await()

        val adsTreeMap = TreeMap<String, Ad>()

        for (doc in docsSnapshot) {
            val ad = firestore.collection("Ads").document(doc.id).get().await()
                .toObject(Ad::class.java)!!
            var key = "${ad.timestamp}${ad.id}"
            if (location == "Favourites")
                key = if (ad.sellerId == userId) "2$key" else "1$key"
            ad.likesCount = ad.likers.size
            ad.isLiked = ad.likers.contains(userId)

            adsTreeMap[key] = ad
        }
        return adsTreeMap
    }
}