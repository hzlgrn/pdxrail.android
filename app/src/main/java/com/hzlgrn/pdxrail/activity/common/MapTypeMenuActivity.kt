package com.hzlgrn.pdxrail.activity.common

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.activity.R

@SuppressLint("Registered")
abstract class MapTypeMenuActivity : GoogleMapViewActivity() {

    private val preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == Domain.App.PREFERENCE.MENU_MAP_TYPE.type) invalidateOptionsMenu()
        }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.google_map, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        for (menuItemIndex in 0 until menu.size()) {
            menu.getItem(menuItemIndex).subMenu?.let { subMenu ->
                for (subMenuIndex in 0 until subMenu.size()) {
                    subMenu.getItem(subMenuIndex).let {
                        if (it.groupId == R.id.group_map_type) {
                            it.isChecked = it.itemId == applicationPreferences.getInt(Domain.App.PREFERENCE.MENU_MAP_TYPE.type, R.id.menu_normal)
                        }
                    }
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPause() {
        super.onPause()
        applicationPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
    }
    override fun onResume() {
        super.onResume()
        applicationPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onMapReady() {
        super.onMapReady()
        mapType(applicationPreferences.getInt(Domain.App.PREFERENCE.MENU_MAP_TYPE.type, R.id.menu_normal))?.let {
            pGoogleMap?.mapType = it
        }.also {
            if (it == null) invalidateOptionsMenu()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val mapType = mapType(item.itemId)
        return if (mapType == null) super.onOptionsItemSelected(item) else true.also {
            pGoogleMap?.mapType = mapType
            applicationPreferences.edit().apply {
                putInt(Domain.App.PREFERENCE.MENU_MAP_TYPE.type, item.itemId)
                apply()
            }
        }
    }
    private fun mapType(itemId: Int): Int? {
        return when (itemId) {
            R.id.menu_terrain -> GoogleMap.MAP_TYPE_TERRAIN
            R.id.menu_satellite -> GoogleMap.MAP_TYPE_SATELLITE
            R.id.menu_normal -> GoogleMap.MAP_TYPE_NORMAL
            R.id.menu_hybrid -> GoogleMap.MAP_TYPE_HYBRID
            else -> null
        }
    }

}
