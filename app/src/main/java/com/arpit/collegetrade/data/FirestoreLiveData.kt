package com.arpit.collegetrade.data

import android.os.Handler
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import timber.log.Timber

class FirestoreLiveData(private val query: Query) : LiveData<QuerySnapshot>() {

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
            registration = query.addSnapshotListener(listener)
        listenerRemovePending = false
    }

    private inner class SnapshotListener : EventListener<QuerySnapshot> {

        private val TAG = "TAG FirestoreLiveData"

        override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
            if (p1 != null) {
                Timber.tag(TAG).e(p1)
                return
            }
            value = p0!!
        }

    }
}