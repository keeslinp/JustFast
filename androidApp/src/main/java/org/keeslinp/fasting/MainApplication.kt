package org.keeslinp.fasting

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import org.keeslinp.fasting.services.AndroidFastNotificationManager
import org.keeslinp.fasting.services.AndroidNotificationScheduler
import org.keeslinp.fasting.services.BackgroundCoroutineManager
import org.keeslinp.fasting.services.NotificationScheduler
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module

@KoinExperimentalAPI
class MainApplication : Application() {
    init {
        println("Init")
        onKoinStartup {
            androidContext(this@MainApplication)
            modules(sharedModule, module {
                single<FastNotificationManager> { AndroidFastNotificationManager() }
                single<NotificationScheduler> { AndroidNotificationScheduler() }
            })
        }
    }
    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleListener())
    }
}

class AppLifecycleListener : DefaultLifecycleObserver, KoinComponent {
    private val manager: BackgroundCoroutineManager by inject()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        manager.start()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        manager.stop()
    }
}