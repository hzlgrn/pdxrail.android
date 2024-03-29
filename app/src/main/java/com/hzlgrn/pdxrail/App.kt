package com.hzlgrn.pdxrail

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.hzlgrn.pdxrail.di.component.ApplicationComponent
import com.hzlgrn.pdxrail.di.component.DaggerForApplicationComponent
import com.hzlgrn.pdxrail.di.module.ApplicationPreferencesModule
import com.hzlgrn.pdxrail.di.module.RetrofitServiceModule
import com.hzlgrn.pdxrail.di.module.RoomDatabaseModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class App : Application(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    private fun growTree(): Timber.Tree {
        return if (BuildConfig.DEBUG) Timber.DebugTree()
        else object: Timber.Tree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                when (priority) {
                    Log.ASSERT, Log.ERROR -> with(FirebaseCrashlytics.getInstance()) {
                        log("$tag: $message")
                        t?.let { recordException(it) }
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(growTree())

        applicationComponent = DaggerForApplicationComponent.builder()
            .applicationPreferencesModule(ApplicationPreferencesModule(applicationContext))
            .roomDatabaseModule(RoomDatabaseModule(applicationContext, this))
            .retrofitServiceModule(RetrofitServiceModule())
            .build()
    }

    companion object {
        lateinit var applicationComponent: ApplicationComponent
    }
}