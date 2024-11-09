package com.djatar.ardath

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.djatar.ardath.feature.presentation.utils.NotificationUtil
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class ArdathApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
        firebaseAnalytics = Firebase.analytics

        NotificationUtil.createNotificationChannel()
    }

    companion object {
        lateinit var applicationScope: CoroutineScope
        lateinit var firebaseAnalytics: FirebaseAnalytics

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}