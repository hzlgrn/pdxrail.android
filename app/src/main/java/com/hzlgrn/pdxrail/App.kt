package com.hzlgrn.pdxrail

import android.app.Application
import android.util.Log
import com.hzlgrn.pdxrail.activity.BuildConfig
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

    private val GiantSequoia by lazy(false) { object: Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            when (priority) { Log.VERBOSE, Log.DEBUG -> return }
            //Crashlytics.logException(err?:Throwable("$tag: $message"))
        }
    }}

    override fun onCreate() {
        super.onCreate()
        Timber.plant(if (BuildConfig.DEBUG) Timber.DebugTree() else GiantSequoia)

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