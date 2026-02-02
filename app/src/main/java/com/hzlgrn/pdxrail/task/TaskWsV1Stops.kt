package com.hzlgrn.pdxrail.task

import android.annotation.SuppressLint
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.Domain.RailSystem.isInCity
import com.hzlgrn.pdxrail.Domain.RailSystem.isNWLovejoyAND22nd
import com.hzlgrn.pdxrail.Domain.RailSystem.isPioneerPlace
import com.hzlgrn.pdxrail.data.net.RailSystemService
import com.hzlgrn.pdxrail.data.room.ApplicationRoom
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException
import java.util.Date
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TaskWsV1Stops: CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineExceptionHandler
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        }



    fun flowLocid(latlng: LatLng, isStreetCar: Boolean): Flow<List<Long>> = flow {
        emit(emptyList())
    }

    // tighten radial search for deep downtown areas where stops are close to each other
    // top left: 45.536915, -122.698876
    // bottom right: 45.504861, -122.667121

    // NW Lovejoy and 22nd Portland Streetcar need 200ft but doesn't pull other stops which makes it a safe call.

    // val is5thAndMill = lat == 45.511739755453917 && lon == -122.68145937416968



    companion object {
        private const val THROTTLE_LOCID = 86400000L  // a day
    }
}