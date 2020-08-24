package com.arpit.collegetrade.data

import android.os.Handler
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import timber.log.Timber

class FirestoreDocRefLiveData(private val doc: DocumentReference) : LiveData<DocumentSnapshot>() {

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

        private val TAG = "TAG QueryLiveData"

        override fun onEvent(p0: DocumentSnapshot?, p1: FirebaseFirestoreException?) {
            if (p1 != null) {
                Timber.tag(TAG).e(p1)
                return
            }
            value = p0!!
        }
    }
}