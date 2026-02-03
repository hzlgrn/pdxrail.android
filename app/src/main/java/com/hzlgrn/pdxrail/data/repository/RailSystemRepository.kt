package com.hzlgrn.pdxrail.data.repository

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.collection.LruCache
import com.google.android.gms.maps.model.LatLng
import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.Domain.RailSystem.isInCity
import com.hzlgrn.pdxrail.Domain.RailSystem.isNWLovejoyAND22nd
import com.hzlgrn.pdxrail.Domain.RailSystem.isPioneerPlace
import com.hzlgrn.pdxrail.data.net.RailSystemService
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapItem
import com.hzlgrn.pdxrail.data.repository.viewmodel.RailSystemMapViewModel
import com.hzlgrn.pdxrail.data.room.dao.ArrivalDao
import com.hzlgrn.pdxrail.data.room.dao.RailSystemDao
import com.hzlgrn.pdxrail.data.room.entity.ArrivalEntity
import com.hzlgrn.pdxrail.data.room.entity.BlockPositionEntity
import com.hzlgrn.pdxrail.data.room.entity.LocIdEntity
import com.hzlgrn.pdxrail.data.room.entity.toRailSystemMapItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.io.IOException
import java.util.Date
import javax.inject.Inject

interface RailSystemRepository {
    fun flowRailSystemMap(): Flow<RailSystemMapViewModel>
    fun flowRailSystemMapItems(): Flow<List<RailSystemMapItem>>
    fun getLocIds(latLng: LatLng, isStreetCar: Boolean): List<Long>
    fun flowArrivals(locIds: LongArray, isStreetcar: Boolean): Flow<List<ArrivalEntity>>
}

class RailSystemRepositoryImpl @Inject constructor(
    private val railSystemDao: RailSystemDao,
    private val arrivalDao: ArrivalDao,
    private val railSystemService: RailSystemService,
): RailSystemRepository {

    override fun flowRailSystemMap(): Flow<RailSystemMapViewModel> {
        return combine(railSystemDao.railStops(), railSystemDao.railLines()) { stops, lines ->
            val stopsViewModel = stops.map {
                RailSystemMapViewModel.RailStopMapViewModel(
                        uniqueid = it.uniqueid,
                        station = it.station,
                        line = it.line,
                        type = it.type,
                        position = LatLng(it.latitude, it.longitude)
                )
            }
            val linesViewModel = lines.map { entity ->
                val polyline = ArrayList<LatLng>()
                val splits = entity.polylineString.split(" ")
                splits.forEach {
                    val split = it.split(",")
                    if (split.count() == 2) {
                        polyline.add(LatLng(split[0].toDouble(), split[1].toDouble()))
                    }
                }
                RailSystemMapViewModel.RailLineMapViewModel(
                        line = entity.line,
                        passage = entity.passage,
                        type = entity.type,
                        polyline = polyline
                )
            }
            RailSystemMapViewModel(stopsViewModel, linesViewModel)
        }
    }

    override fun flowRailSystemMapItems(): Flow<List<RailSystemMapItem>> {
        return combine(railSystemDao.railStops(), railSystemDao.railLines()) { stops, lines ->
            stops.map { it.toRailSystemMapItem() } + lines.map { it.toRailSystemMapItem() }
        }
    }

    // tighten radial search for deep downtown areas where stops are close to each other
    // top left: 45.536915, -122.698876
    // bottom right: 45.504861, -122.667121

    // NW Lovejoy and 22nd Portland Streetcar need 200ft but doesn't pull other stops which makes it a safe call.

    // val is5thAndMill = lat == 45.511739755453917 && lon == -122.68145937416968

    @SuppressLint("ApplySharedPref")
    override fun getLocIds(latLng: LatLng, isStreetCar: Boolean): List<Long> {
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

        val lastUpdated = arrivalDao.getLocIdFor("$lat,$lon")?.updated ?: 0L
        Timber.d("lastsuccess-locids-$lat,$lon: ${now-lastUpdated} > $THROTTLE_LOCID = ${(now-lastUpdated) > THROTTLE_LOCID}")
        if ((now-lastUpdated) > THROTTLE_LOCID) {
            wsV1Stops(radiusInFeet, lat, lon, isStreetCar)
        }
        val listLocId = mutableListOf<Long>()
        val latestModel = arrivalDao.getLocIdFor("$lat,$lon")
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

        return listLocId
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
                            Timber.d("found ${it.size} locations")
                        }

                        if (!locations.isNullOrEmpty()) {
                            val commaSeparatedLocId = locations.filter {
                                isStreetCar
                                        || it.desc?.contains(Domain.RailSystem.MAX, true) == true
                                        || it.desc?.contains(Domain.RailSystem.WES, true) == true
                            }.joinToString(separator = ",") { it.locid.toString() }

                            arrivalDao.updateOrReplace(
                                LocIdEntity(
                                    latlon = "$lat,$lon",
                                    updated = Date().time,
                                    csvlocid = commaSeparatedLocId
                                ).also {
                                    with(it) {
                                        Timber.d("updateOrReplace(latlon = $latlon updated = $updated csvLocId = $csvlocid")
                                    }
                                }
                            )
                        }

                    }
                }
            }
        } catch (err: Exception) {
            Timber.e(err)
        }
    }

    override fun flowArrivals(locIds: LongArray, isStreetcar: Boolean): Flow<List<ArrivalEntity>> = flow {
        while(true) {
            emit(wsV2Arrivals(locIds, isStreetcar))
            delay(THROTTLE_ARRIVALS)
        }
    }

    private fun wsV2Arrivals(locId: LongArray, isStreetCar: Boolean): List<ArrivalEntity> {
        val arrivals = mutableListOf<ArrivalEntity>()
        val memoryKey = "arrivals-$locId-updated"
        val now = SystemClock.elapsedRealtime()
        val lastArrivalFetch = MEMORY[memoryKey] ?: 0L
        if (now - lastArrivalFetch > THROTTLE_ARRIVALS) {
            try {
                val csvLocId = locId.joinToString(",")
                if (csvLocId.isBlank()) {
                    Timber.e("csvLocId is blank!")
                } else {
                    railSystemService.wsV2Arrivals(csvLocId, isStreetCar).also { call ->
                        Timber.d(call.request().url.toString())
                    }.execute().also { response ->

                        if (!response.isSuccessful) {
                            throw IOException("Unexpected code: ${response.code()} message: ${response.message()}")
                        } else {
                            // TODO: Expand List<ArrivalEntity> to a display model
                            val blockPositions = mutableListOf<BlockPositionEntity>()
                            response.body()?.resultSet?.arrival?.let { arrivalResults ->
                                for (arrival in arrivalResults) {
                                    val arrivalModel = ArrivalEntity(arrival)
                                    if (isStreetCar) {
                                        val isValid = arrival.fullSign.contains(Domain.RailSystem.STREETCAR_IN_FULLSIGN, true)
                                        if (isValid) {
                                            arrival.blockPosition?.let { blockPosition ->
                                                arrivalModel.blockPositionId = blockPosition.id
                                                blockPositions.add(BlockPositionEntity(blockPosition))
                                            }
                                            arrivals.add(arrivalModel)
                                        }
                                    } else {
                                        val isValid = arrival.fullSign.contains(Domain.RailSystem.MAX, true)
                                                || arrival.fullSign.contains(Domain.RailSystem.WES, true)
                                        if (isValid) {
                                            arrival.blockPosition?.let { blockPosition ->
                                                arrivalModel.blockPositionId = blockPosition.id
                                                blockPositions.add(BlockPositionEntity(blockPosition))
                                            }
                                            arrivals.add(arrivalModel)
                                        }
                                    }
                                }

                            }

                            arrivalDao
                                .updateArrivals(locId.toList(), blockPositions, arrivals)
                            MEMORY.put(memoryKey, SystemClock.elapsedRealtime())
                        }
                    }
                }
            } catch (err: Exception) { Timber.e(err) }
        }

        return arrivals

    }

    companion object {
        private const val THROTTLE_LOCID = 86400000L  // a day
        const val THROTTLE_ARRIVALS = 10000L // 10 seconds
        private val MEMORY = LruCache<String, Long>(10) // TODO: DI and inject?
    }

}