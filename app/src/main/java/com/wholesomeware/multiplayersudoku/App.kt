package com.wholesomeware.multiplayersudoku

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class App : Application() {

    lateinit var firestore: FirebaseFirestore
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        firestore = Firebase.firestore
    }

    companion object {
        lateinit var instance: App
    }
}