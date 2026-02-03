package com.hzlgrn.pdxrail.activity.presenter

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.hzlgrn.pdxrail.BuildConfig
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.activity.MainActivity
import com.hzlgrn.pdxrail.databinding.ActivityMainBinding
import timber.log.Timber
import java.util.Calendar


class MainActivityPresenter(private val activity: MainActivity, private val mBinding: ActivityMainBinding) {

    val drawerToggle by lazy {
        ActionBarDrawerToggle(activity, mBinding.drawerLayout, R.string.drawer_opened, R.string.drawer_closed)
    }

    var focusOnStopUniqueId: String? = null
        set(uniqueId) {
            Timber.d("StopUniqueId = $uniqueId")
            if (googleMap != null && !uniqueId.isNullOrEmpty()) {
                activity.selectStop(uniqueId)
            }
            field = uniqueId
        }

    private var googleMap: GoogleMap? = null
    private val isDrawerOpen: Boolean get() = mBinding.drawerLayout.isDrawerOpen(mBinding.drawerStart.root)

    fun onSetContentView() {
        mBinding.drawerLayout.apply {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            setScrimColor(ResourcesCompat.getColor(resources, android.R.color.transparent, activity.theme))
            addDrawerListener(drawerToggle)
            addDrawerListener(object: DrawerLayout.DrawerListener {
                override fun onDrawerStateChanged(newState: Int) {}
                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    drawerAdjust((slideOffset*mBinding.drawerStart.drawerBackground.width).toInt())
                }
                override fun onDrawerClosed(drawerView: View) {
                    this@MainActivityPresenter.onDrawerClosed()
                }
                override fun onDrawerOpened(drawerView: View) {
                    this@MainActivityPresenter.onDrawerOpened(drawerView)
                }
            })
            closeDrawer(GravityCompat.START)
        }
        activity.setSupportActionBar(mBinding.toolbarMain)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        activity.supportActionBar?.setHomeButtonEnabled(true)

        mBinding.drawerStart.textVersion.text = activity.getString(R.string.version, BuildConfig.VERSION_NAME)

        fun formatDate(millis: Long): String {
            val formatter = android.text.format.DateFormat.getMediumDateFormat(activity)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = millis
            return formatter.format(calendar.time)
        }

        mBinding.drawerStart.textBuildTime.text = formatDate(BuildConfig.BUILD_TIME)
        mBinding.drawerStart.actionRate.setOnClickListener {
            val uri = Uri.parse("market://details?id=${activity.packageName}")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                    or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                    or Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            try {
                activity.startActivity(goToMarket)
            } catch (err: ActivityNotFoundException) {
                val fallbackUri = Uri.parse("http://play.google.com/store/apps/details?id=${activity.packageName}")
                activity.startActivity(Intent(Intent.ACTION_VIEW, fallbackUri))
            }
        }

        mBinding.toggler.setOnClickListener {
            onOptionItemSelected(R.id.toggler)
        }
    }

    fun onBackPressed(): Boolean {
        return if (mBinding.drawerLayout.isDrawerOpen(mBinding.drawerStart.drawerStart)) {
            mBinding.drawerLayout.closeDrawer(mBinding.drawerStart.drawerStart, true)
            true
        } else {
            false
        }
    }

    fun onDrawerClosed() {
        (activity.getStopMarkerFor(focusOnStopUniqueId)?.position?:googleMap?.cameraPosition?.target)?.let { center ->
            drawerAdjust()
            googleMap?.apply {
                setPadding(0, 0, 0, 0)
                animateCamera(CameraUpdateFactory.newLatLng(center))
            }
        }
        activity.invalidateOptionsMenu()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun onDrawerOpened(drawerView: View) {
        drawerView.setOnTouchListener { _, event -> (event?.rawX?.toInt() ?: Int.MAX_VALUE) < mBinding.drawerStart.drawerBackground.width }
        (activity.getStopMarkerFor(focusOnStopUniqueId)?.position?:googleMap?.cameraPosition?.target)?.let { center ->
            drawerAdjust(mBinding.drawerStart.drawerBackground.width)
            googleMap?.apply {
                setPadding(mBinding.drawerStart.drawerBackground.width, 0, 0, 0)
                animateCamera(CameraUpdateFactory.newLatLng(center))
            }
        }
        activity.invalidateOptionsMenu()
    }

    fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (!focusOnStopUniqueId.isNullOrEmpty()) {
            focusOnStopUniqueId?.let { activity.selectStop(it) }
        }
    }

    fun openDrawer(animate: Boolean = true) {
        mBinding.drawerLayout.openDrawer(GravityCompat.START, animate)
    }


    fun onOptionItemSelected(item: MenuItem): Boolean {
        return onOptionItemSelected(item.itemId)
    }

    private fun onOptionItemSelected(itemId: Int): Boolean {
        return when (itemId) {
            android.R.id.home -> true.also {
                if (isDrawerOpen) {
                    mBinding.drawerLayout.closeDrawer(mBinding.drawerStart.root, true)
                } else {
                    mBinding.drawerLayout.openDrawer(mBinding.drawerStart.root, true)
                }
            }
            R.id.toggler -> true.also {
                if (isDrawerOpen) {
                    mBinding.drawerLayout.closeDrawer(mBinding.drawerStart.root, true)
                } else {
                    mBinding.drawerLayout.openDrawer(mBinding.drawerStart.root, true)
                }
            }
            R.id.menu_help -> true.also {
                activity.showHelpDialog(true)
            }
            R.id.menu_share -> true.also {
                if (focusOnStopUniqueId?.isNotEmpty() == true) {
                    val shareBody = "${BuildConfig.HOME_URL}$focusOnStopUniqueId"
                    val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, activity.getString(R.string.menu_share_extra_subject))
                        putExtra(Intent.EXTRA_TEXT, shareBody)
                    }
                    activity.startActivity(Intent.createChooser(sharingIntent, activity.getString(R.string.share_using)))
                } else {
                    val actionBarHeight = mBinding.toolbarMain.height
                    Toast.makeText(activity, R.string.toast_fail_share, Toast.LENGTH_LONG).apply {
                        setGravity(Gravity.TOP, 0, actionBarHeight + activity.resources.getDimensionPixelOffset(R.dimen.vertical_small))
                        show()
                    }
                }

            }
            else -> false
        }
    }

    private fun drawerAdjust(startMargin: Int = 0) {
        if (mBinding.guideGoogleMapLogoWidthFromStart.layoutParams is MarginLayoutParams) {
            val p = mBinding.guideGoogleMapLogoWidthFromStart.layoutParams as MarginLayoutParams
            p.setMargins(startMargin, 0, 0, 0)
            mBinding.guideGoogleMapLogoWidthFromStart.requestLayout()
        }
    }

}