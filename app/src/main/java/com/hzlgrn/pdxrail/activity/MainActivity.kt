package com.hzlgrn.pdxrail.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.databinding.ActivityMainBinding
import com.hzlgrn.pdxrail.databinding.DrawerArrivalsBinding
import com.hzlgrn.pdxrail.activity.presenter.MainActivityPresenter
import com.hzlgrn.pdxrail.activity.railsystem.RailSystemStopActivity
import timber.log.Timber

class MainActivity : RailSystemStopActivity() {

    private lateinit var mBinding: ActivityMainBinding
    override val pDrawerBinding: DrawerArrivalsBinding by lazy {
        mBinding.drawerStart
    }
    override val pMapView: MapView by lazy {
        mBinding.mapView
    }

    private var mIntentFromIntent: Boolean = false

    private val mPresenter by lazy { MainActivityPresenter(this, mBinding) }

    override var pFocusStopUniqueId: String? = null
        get() = super.pFocusStopUniqueId
        set(value) {
            field = value
            mPresenter.focusOnStopUniqueId = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val didHandleIntent = handleIntent(intent)
        Timber.d("didHandleIntent = $didHandleIntent")
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val didHandleIntent = handleIntent(intent)
        Timber.d("didHandleIntent = $didHandleIntent")
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        mPresenter.onSetContentView()
    }
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mPresenter.drawerToggle.syncState()
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mPresenter.drawerToggle.onConfigurationChanged(newConfig)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onMapReady() {
        super.onMapReady()
        pGoogleMap?.let { mPresenter.onMapReady(it) }
    }
    override fun onBackPressed() {
        if (!mPresenter.onBackPressed()) super.onBackPressed()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (mPresenter.onOptionItemSelected(item)) true else super.onOptionsItemSelected(item)
    }

    override fun onInfoWindowClick(marker: Marker?) {
        mPresenter.openDrawer()
    }


    private fun handleIntent(intent: Intent?): Boolean {
        Timber.d(intent?.toString()?:"NULL")
        return when (intent?.action) {
            Intent.ACTION_VIEW -> {
                val data = intent.data?.toString()
                if (data.isNullOrEmpty()) false else {
                    handleActionViewIntent(data)
                }
            }
            else -> false
        }
    }
    private fun handleActionViewIntent(data: String): Boolean {
        return when {
            data.contains(BuildConfig.HOME_HOST) -> {
                mIntentFromIntent = true
                mPresenter.focusOnStopUniqueId = data // TODO: This might be broken.
                true
            }

            //--- https://developer.android.com/guide/components/intents-common
            data.contains(Domain.Intent.GEO) -> {
                handleActionViewGeo(data)
                true
            }
            else -> false
        }
    }
    private fun handleActionViewGeo(data: String) {
        try {
            val args = if (data.contains("?")) data.substringAfter("?") else ""
            val strLatLng = data.removePrefix(Domain.Intent.GEO).removeSuffix("?$args").split(",")
            val lat = strLatLng[1].toDouble()
            val lon = strLatLng[0].toDouble()
            val geoPosition = LatLng(lat, lon)
            moveMapTo(geoPosition)
            Timber.d("lat($lat), lon($lon), args($args)")
        } catch (err: Exception) {
            if (BuildConfig.DEBUG) err.printStackTrace()
        }
    }

}
