package com.djatar.ardath

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class ArdathApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        applicationScope = CoroutineScope(SupervisorJob())
    }

    companion object {
        lateinit var applicationScope: CoroutineScope

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}