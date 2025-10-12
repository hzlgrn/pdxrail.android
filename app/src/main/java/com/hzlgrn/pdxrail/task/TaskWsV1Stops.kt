package com.hzlgrn.pdxrail.task

import android.annotation.SuppressLint
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.App
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
import okhttp3.internal.toImmutableList
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class TaskWsV1Stops: CoroutineScope {

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
        val now = Date().time
        val lat = latLng.latitude
        val lon = latLng.longitude
        Timber.d("Looking for ${if(isStreetCar) "StreetCar" else "MAX"} arrivals at $lat, $lon")

        val radiusInFeet: Long = when {
            latLng.isNWLovejoyAND22nd() -> 200L
            isStreetCar -> 50L
            latLng.isPioneerPlace() -> 100L
            latLng.isInCity() -> 125L
            else -> 200L
        }

        val lastUpdated = applicationRoom.arrivalDao().getLocIdFor("$lat,$lon")?.updated ?: 0L
        Timber.d("lastsuccess-locids-$lat,$lon: ${now-lastUpdated} > $THROTTLE_LOCID = ${(now-lastUpdated) > THROTTLE_LOCID}")
        if ((now-lastUpdated) > THROTTLE_LOCID) {
            wsV1Stops(radiusInFeet, lat, lon, isStreetCar)
        }
        val listLocId = mutableListOf<Long>()
        val latestModel = applicationRoom.arrivalDao().getLocIdFor("$lat,$lon")
        val csvLocId = latestModel?.csvlocid
        val split = csvLocId?.split(',').orEmpty()
        for (locId in split) {
            try {
                if (locId.isEmpty()) Timber.e("empty locId")
                else {
                    val locIdLong = locId.toLong()
                    listLocId.add(locIdLong)
                }
            } catch(err: NumberFormatException) { Timber.e(err) }
        }

        return listLocId.toImmutableList()
    }

    private fun wsV1Stops(radiusInFeet: Long, lat: Double, lon: Double, isStreetCar: Boolean) {
        Timber.d("wsV1Stops($radiusInFeet, $lat, $lon, $isStreetCar)")
        try {
            railSystemService.wsV1Stops(radiusInFeet, "$lat, $lon", isStreetCar).also { call ->
                Timber.d(call.request().url.toString())
            }.execute().also { response ->

                if (!response.isSuccessful) {
                    throw IOException("Unexpected code: ${response.code()} message: ${response.message()}")
                } else {

                    response.body()?.let { wsV1StopsResponse ->
                        val locations = wsV1StopsResponse.resultSet.location?.also {
                            Timber.d( "found ${it.size} locations")
                        }

                        if (!locations.isNullOrEmpty()) {
                            val commaSeparatedLocId = locations.filter {
                                isStreetCar
                                        || it.desc?.contains(Domain.RailSystem.MAX, true) == true
                                        || it.desc?.contains(Domain.RailSystem.WES, true) == true
                            }.joinToString(separator = ",") { it.locid.toString() }

                            applicationRoom.arrivalDao().updateOrReplace(
                                LocIdEntity(
                                    latlon = "$lat,$lon",
                                    updated = Date().time,
                                    csvlocid = commaSeparatedLocId
                                ).also { with (it) {
                                    Timber.d("updateOrReplace(latlon = $latlon updated = $updated csvLocId = $csvlocid")
                                } }
                            )
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