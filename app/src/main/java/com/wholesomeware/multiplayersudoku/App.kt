package com.wholesomeware.multiplayersudoku

import android.app.Application
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class App: Application() {

    val firestore = Firebase.firestore

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: App
    }
}