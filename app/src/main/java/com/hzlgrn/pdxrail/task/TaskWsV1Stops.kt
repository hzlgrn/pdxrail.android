package com.hzlgrn.pdxrail.task

import android.annotation.SuppressLint
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.App
import com.hzlgrn.pdxrail.Domain
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
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TaskTrimetWsV1Stops: CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineExceptionHandler
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }

    @Inject
    lateinit var applicationRoom: ApplicationRoom

    @Inject
    lateinit var railSystemService: RailSystemService

    init { App.applicationComponent.inject(this) }

    fun flowLocid(latlng: LatLng, isStreetCar: Boolean): Flow<List<Long>> = flow {
        emit(fetchLocIds(latlng, isStreetCar))
    }

    // tighten radial search for deep downtown areas where stops are close to each other
    // top left: 45.536915, -122.698876
    // bottom right: 45.504861, -122.667121

    // NW Lovejoy and 22nd Portland Streetcar need 200ft but doesn't pull other stops which makes it a safe call.

    // val is5thAndMill = lat == 45.511739755453917 && lon == -122.68145937416968

    @SuppressLint("ApplySharedPref")
    private fun fetchLocIds(latLng: LatLng, isStreetCar: Boolean): List<Long> {
        val lat = latLng.latitude
        val lon = latLng.longitude
        Timber.d("Looking for ${if(isStreetCar) "StreetCar" else "MAX"} arrivals at $lat, $lon")

        val isNWLovejoyAND22nd = (lat==45.52974474648903 && lon==-122.69688015146481)
        val isPioneerPlace = (lat==45.51849907171159 && lon==-122.6777873516982)
        val isInCity = lat < 45.536915 && lat > 45.504861 && lon > -122.698876 && lon < -122.667121
        val radiusInFeet: Long = when {
            isNWLovejoyAND22nd -> 200L
            isStreetCar -> 50L
            isPioneerPlace -> 100L
            isInCity -> 125L
            else -> 200L
        }

        val now = Date().time
        val lastUpdated = applicationRoom.arrivalDao().getLocIdFor("$lat,$lon")?.updated ?: 0L
        Timber.d("lastsuccess-locids-$lat,$lon: ${now-lastUpdated} > $THROTTLE_LOCID = ${(now-lastUpdated) > THROTTLE_LOCID}")
        if ((now-lastUpdated) > THROTTLE_LOCID) {
            wsV1Stops(radiusInFeet, lat, lon, isStreetCar)
        }
        val listLocId = mutableListOf<Long>()
        val latestModel = applicationRoom.arrivalDao().getLocIdFor("$lat,$lon")
        val csvLocId = latestModel?.csvlocid
        val split = csvLocId?.split(',') ?: emptyList()
        for (locid in split) {
            try {
                val locidlong = locid.toLong()
                listLocId.add(locidlong)
            } catch(err: NumberFormatException) { Timber.e(err) }
        }

        return listLocId
    }

    private fun wsV1Stops(radiusInFeet: Long, lat: Double, lon: Double, isStreetCar: Boolean) {
        Timber.d("wsV1Stops($radiusInFeet, $lat, $lon, $isStreetCar)")
        try {
            railSystemService.wsV1Stops(radiusInFeet, "$lat, $lon", isStreetCar).also {
                Timber.d(it.request().url.toString())
            }.execute().also { response ->

                if (!response.isSuccessful) {
                    throw IOException("Unexpected code: ${response.code()} message: ${response.message()}")
                } else {

                    response.body()?.let { wsV1StopsResponse ->
                        val locations = wsV1StopsResponse.resultSet.location?.also {
                            Timber.d( "found ${it.size} locations ${it.run {
                                var locids = ""
                                for (location in this) {
                                    if (location.locid != this.first().locid) locids += ","
                                    locids += "${location.locid}"
                                }
                                locids
                            } }")
                        }

                        if (!locations.isNullOrEmpty()) {
                            val commaSeparatedLocId = StringBuilder()
                            for (location in locations) {
                                if (isStreetCar) {
                                    if (commaSeparatedLocId.isNotEmpty()) {
                                        commaSeparatedLocId.append(",")
                                    }
                                    commaSeparatedLocId.append(location.locid)
                                } else {
                                    location.desc?.let {
                                        if (it.contains(Domain.RailSystem.MAX, true)
                                                || it.contains(Domain.RailSystem.WES, true)) {
                                            if (commaSeparatedLocId.isNotEmpty()) {
                                                commaSeparatedLocId.append(",")
                                            }
                                            commaSeparatedLocId.append(location.locid)
                                        }
                                    }
                                }
                            }

                            applicationRoom.arrivalDao().updateOrReplace(
                                    LocIdEntity(
                                            latlon = "$lat,$lon",
                                            updated = Date().time,
                                            csvlocid = commaSeparatedLocId.toString()))
                        }
                    }
                }
            }
        } catch (err: Exception) { Timber.e(err) }
    }

    companion object {
        private const val THROTTLE_LOCID = 86400000L  // a day
    }
}