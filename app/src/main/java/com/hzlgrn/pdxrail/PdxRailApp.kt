package com.hzlgrn.pdxrail

import android.app.Application
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
public class PdxRailApp : Application() {
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
    }
}