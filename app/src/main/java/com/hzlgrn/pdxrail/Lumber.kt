package com.hzlgrn.pdxrail

import android.annotation.SuppressLint
import android.util.Log
import org.json.JSONObject
import kotlin.math.floor

@Suppress("unused")
object Lumber {

    @SuppressLint("LogNotTimber")
    fun v(tag: String, json: String, planksPerBundle: Int = 100) {
        try {
            val linesOfJSON = JSONObject(json).toString(1).lines()
            val bundle = StringBuilder()
            var bundleCurrent = 0
            val bundleTotal = floor(linesOfJSON.size.toDouble() / planksPerBundle).toInt()
            var plank = 0
            linesOfJSON.forEach { line ->
                bundle.append("\n").append(line)
                plank++
                if (plank >= planksPerBundle) {
                    Log.v(tag, "\uD83E\uDE93[$bundleCurrent / $bundleTotal]\uD83E\uDE93$bundle")
                    bundle.clear()
                    bundleCurrent++
                    plank = 0
                }
            }
            Log.v(tag, "\uD83E\uDE93[$bundleTotal / $bundleTotal]\uD83E\uDE93$bundle")
        } catch (err: Throwable) {
            err.printStackTrace()
        }
    }

}