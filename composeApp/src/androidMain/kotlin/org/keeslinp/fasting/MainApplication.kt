package org.keeslinp.fasting

import android.app.Application
import org.koin.android.ext.koin.androidContext

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startApp {
            androidContext(this@MainApplication)
        }
    }
}