package com.hzlgrn.pdxrail

import androidx.compose.ui.window.ComposeUIViewController
import com.hzlgrn.pdxrail.di.IosModule
import com.hzlgrn.pdxrail.di.repositoryModule
import com.hzlgrn.pdxrail.di.viewModelModule
import org.koin.mp.KoinPlatformTools
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIViewController
import platform.darwin.NSObject

// Module-level refs keep delegate and manager alive past MainViewController() return.
private var locationManager: CLLocationManager? = null
private var locationDelegate: LocationPermissionDelegate? = null

// Bridge between native CLLocationManager callbacks and the Compose ViewModel.
internal val locationAuthorized = kotlinx.coroutines.flow.MutableStateFlow(false)

private class LocationPermissionDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        val status = manager.authorizationStatus
        locationAuthorized.value = status == kCLAuthorizationStatusAuthorizedWhenInUse ||
                status == kCLAuthorizationStatusAuthorizedAlways
    }
}

private fun openUrl(url: String) {
    NSURL.URLWithString(url)?.let {
        UIApplication.sharedApplication.openURL(it, options = emptyMap<Any?, Any?>(), completionHandler = null)
    }
}

fun MainViewController(): UIViewController {
    // Start Koin only once: this factory can run more than once (e.g. when the
    // view controller is recreated), and starting a second Koin application
    // crashes with KoinApplicationAlreadyStartedException.
    val koinContext = KoinPlatformTools.defaultContext()
    koinContext.getOrNull() ?: koinContext.startKoin {
        modules(IosModule.all + repositoryModule + viewModelModule)
    }

    val manager = CLLocationManager()
    val delegate = LocationPermissionDelegate()
    manager.delegate = delegate
    locationManager = manager
    locationDelegate = delegate

    // Seed the flow and request if not yet determined.
    val status = manager.authorizationStatus
    locationAuthorized.value = status == kCLAuthorizationStatusAuthorizedWhenInUse ||
            status == kCLAuthorizationStatusAuthorizedAlways
    if (!locationAuthorized.value) manager.requestWhenInUseAuthorization()

    return ComposeUIViewController(configure = {
        enforceStrictPlistSanityCheck = false
    }) {
        App(
            onReviewClick = { openUrl("https://apps.apple.com/app/id6759946671?action=write-review") },
            onLicensesClick = { openUrl("https://pdxrail.hzlgrn.com/licenses") },
            onRequestLocationPermission = {
                when (manager.authorizationStatus) {
                    kCLAuthorizationStatusAuthorizedWhenInUse,
                    kCLAuthorizationStatusAuthorizedAlways -> locationAuthorized.value = true
                    kCLAuthorizationStatusDenied,
                    kCLAuthorizationStatusRestricted -> openUrl(UIApplicationOpenSettingsURLString)
                    else -> manager.requestWhenInUseAuthorization()
                }
            },
        )
    }
}
