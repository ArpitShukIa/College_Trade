package com.arpit.collegetrade.data

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*

class FirestoreDocRefLiveData(private val doc: DocumentReference) : LiveData<DocumentSnapshot?>() {

    private val listener = SnapshotListener()
    private var registration: ListenerRegistration? = null

    private var listenerRemovePending = false
    private val handler = Handler()

    private val removeListener = Runnable {
        registration?.remove()
        listenerRemovePending = false
    }

    override fun onInactive() {
        super.onInactive()
        handler.postDelayed(removeListener, 1000)
        listenerRemovePending = true
    }

    override fun onActive() {
        if (listenerRemovePending)
            handler.removeCallbacks(removeListener)
        else
            registration = doc.addSnapshotListener(MetadataChanges.INCLUDE, listener)
        listenerRemovePending = false
    }

    private inner class SnapshotListener : EventListener<DocumentSnapshot> {

        private val TAG = "TAG FirestoreLiveData"

        override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
            if (p1 != null) {
                Log.e(TAG, "onEvent: Listen Failed ${p1.stackTrace}", p1)
                return
            }
            value = if(p0?.metadata?.isFromCache!!) null else p0
        }

    }
}