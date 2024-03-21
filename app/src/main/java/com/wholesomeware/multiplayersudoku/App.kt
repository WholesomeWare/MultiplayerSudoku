package com.wholesomeware.multiplayersudoku

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class App : Application() {
    companion object {
        lateinit var instance: App
    }

    lateinit var firestore: FirebaseFirestore
        private set
    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate() {
        super.onCreate()
        instance = this

        appUpdateManager = AppUpdateManagerFactory.create(this)
        firestore = Firebase.firestore
    }

    fun requestUpdateIfAvailable(
        activityResultLauncher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        appUpdateManager.appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activityResultLauncher,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                    )
                }
            }
    }
}