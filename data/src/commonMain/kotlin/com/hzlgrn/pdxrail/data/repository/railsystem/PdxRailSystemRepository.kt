package com.hzlgrn.pdxrail.data.repository.railsystem

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.hzlgrn.pdxrail.data.db.AppDatabase
import com.hzlgrn.pdxrail.data.geo.LatLon
import com.hzlgrn.pdxrail.data.help.PdxRailSystemHelper.radiusPingLocIds
import com.hzlgrn.pdxrail.data.model.ArrivalItemData
import com.hzlgrn.pdxrail.data.model.ArrivalMarkerData
import com.hzlgrn.pdxrail.data.model.RailSystemMapData
import com.hzlgrn.pdxrail.data.model.net.WsV2ArrivalsResponse
import com.hzlgrn.pdxrail.data.net.PdxRailSystemClient
import com.hzlgrn.pdxrail.data.repository.RailSystemRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlin.time.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PdxRailSystemRepository(
    private val appDatabase: AppDatabase,
    private val pdxRailSystemClient: PdxRailSystemClient,
    private val settings: Settings,
) : RailSystemRepository {

    override fun flowRailSystemMapDataByRegion(
        north: Double, south: Double, east: Double, west: Double,
    ): Flow<List<RailSystemMapData>> {
        val stopsFlow = appDatabase.railStopQueries.railStopsByRegion(
            north = north, south = south, east = east, west = west,
        )
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { RailSystemMapData.Stop(it.uniqueid, it.station, it.type, it.latitude, it.longitude) } }

        val linesFlow = appDatabase.railLineQueries.railLinesByRegion(
            north = north, south = south, east = east, west = west,
        )
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { RailSystemMapData.Line(it.line, it.passage, it.type, it.polyline_string) } }

        return combine(stopsFlow, linesFlow) { stops, lines -> lines + stops }.conflate()
    }

    override fun flowRailSystemMapData(): Flow<List<RailSystemMapData>> {
        val stopsFlow = appDatabase.railStopQueries.railStops()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { RailSystemMapData.Stop(it.uniqueid, it.station, it.type, it.latitude, it.longitude) } }

        val linesFlow = appDatabase.railLineQueries.railLines()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { RailSystemMapData.Line(it.line, it.passage, it.type, it.polyline_string) } }

        return combine(stopsFlow, linesFlow) { stops, lines -> lines + stops }.conflate()
    }

    override suspend fun getLocIds(latLon: LatLon, isStreetCar: Boolean): List<Long> {
        val now = Clock.System.now().toEpochMilliseconds()
        val lat = latLon.lat
        val lon = latLon.lon

        val radiusInFeet: Long = latLon.radiusPingLocIds(isStreetCar)

        val lastUpdated = appDatabase.locIdQueries.getForLatLon("$lat,$lon").executeAsOneOrNull()?.updated ?: 0L
        if ((now - lastUpdated) > THROTTLE_LOCID) {
            wsV1Stops(radiusInFeet, lat, lon, isStreetCar)
        }

        val csvLocId = appDatabase.locIdQueries.getForLatLon("$lat,$lon").executeAsOneOrNull()?.csvlocid
        return csvLocId?.split(',').orEmpty().mapNotNull { it.toLongOrNull() }
    }

    @OptIn(FlowPreview::class)
    override fun flowArrivalMarkers(
        locIds: List<Long>
    ): Flow<List<ArrivalMarkerData>> {
        val settingsKey = "arrivals-${locIds.joinToString("-")}"
        return appDatabase.arrivalQueries
            .getArrivalMarkers(locIds)
            .asFlow()
            .debounce(DELIVERY_DEBOUNCE_MS.toDuration(DurationUnit.MILLISECONDS))
            .mapToList(Dispatchers.Default)
            .map { list ->
                val now = Clock.System.now().toEpochMilliseconds()
                val lastFetch = settings.getLong(settingsKey, 0L)
                if (now - lastFetch > EXPIRE_ARRIVALS) return@map emptyList()
                list.map {
                    ArrivalMarkerData(
                        it.short_sign,
                        it.lat,
                        it.lng,
                        it.heading.toInt(),
                    )
                }
            }
    }

    override fun foreverGetArrivals(locIds: List<Long>, isStreetCar: Boolean): Flow<Boolean> = flow {
        // Note: in kotlinx-coroutines 1.10 the flow builder receiver (FlowCollector)
        // is no longer a CoroutineScope, so use currentCoroutineContext().
        while (currentCoroutineContext().isActive) {
            // wsV2Arrivals never throws; it reports failures as `false`.
            val didArrivalFetchSucceed = wsV2Arrivals(locIds, isStreetCar)
            emit(didArrivalFetchSucceed)
            delay(THROTTLE_GET_ARRIVALS_S.toDuration(DurationUnit.SECONDS))
        }
    }

    @OptIn(FlowPreview::class)
    override fun flowArrivalItems(locIds: List<Long>): Flow<List<ArrivalItemData>> {
        val settingsKey = "arrivals-${locIds.joinToString("-")}"
        return appDatabase.arrivalQueries.arrivalItemsForLocIds(locIds)
            .asFlow()
            .debounce(DELIVERY_DEBOUNCE_MS.toDuration(DurationUnit.MILLISECONDS))
            .mapToList(Dispatchers.Default)
            .map { rows ->
                val now = Clock.System.now().toEpochMilliseconds()
                val lastFetch = settings.getLong(settingsKey, 0L)
                if (now - lastFetch > EXPIRE_ARRIVALS) return@map emptyList()
                rows.map { row ->
                    ArrivalItemData(
                        scheduled = row.scheduled,
                        estimated = row.estimated,
                        shortSign = row.short_sign,
                        lat = row.lat ?: 0.0,
                        lon = row.lng ?: 0.0,
                        heading = (row.heading ?: 0L).toFloat(),
                    )
                }
            }
    }

    private suspend fun wsV1Stops(radiusInFeet: Long, lat: Double, lon: Double, isStreetCar: Boolean) {
        try {
            val response = pdxRailSystemClient.wsV1Stops(radiusInFeet, lat, lon, isStreetCar)
            val locations = response.resultSet.location
            if (!locations.isNullOrEmpty()) {
                val csvLocId = locations.filter { location ->
                    isStreetCar
                            || location.desc?.contains("MAX", ignoreCase = true) == true
                            || location.desc?.contains("WES", ignoreCase = true) == true
                }.joinToString(",") { it.locid.toString() }

                appDatabase.locIdQueries.upsert(
                    latlon = "$lat,$lon",
                    updated = Clock.System.now().toEpochMilliseconds(),
                    csvlocid = csvLocId,
                )
            }
        } catch (_: Exception) {
            // non-fatal: stale or missing locId cache is handled at caller
        }
    }

    /**
     * Fetches arrivals for [locIds] and writes them to the database.
     *
     * Returns true when the data is current (freshly fetched, or there was
     * nothing to fetch) and false when the request failed. Never throws:
     * failures are reported as false so the [foreverGetArrivals] polling loop
     * survives transient errors and callers can tell a failure apart from a
     * genuinely empty result set.
     */
    private suspend fun wsV2Arrivals(locIds: List<Long>, isStreetCar: Boolean): Boolean {
        val settingsKey = "arrivals-${locIds.joinToString("-")}"

        val csvLocId = locIds.joinToString(",")
        if (csvLocId.isBlank()) return true

        try {
            val response = pdxRailSystemClient.wsV2Arrivals(csvLocId, isStreetCar)

            data class ArrivalRow(
                val id: String, val feet: Long, val inCongestion: Long?, val departed: Long?,
                val scheduled: Long, val loadPercentage: Long?, val shortSign: String?,
                val estimated: Long?, val detoured: Long, val tripId: String?, val dir: Long,
                val blockId: Long, val route: Long, val piece: String?, val fullSign: String?,
                val dropOffOnly: Long?, val vehicleId: String?, val showMilesAway: Long?,
                val locid: Long, val newTrip: Long, val status: String, val blockPositionId: Long?,
            )

            val arrivalRows = mutableListOf<ArrivalRow>()
            val blockPositions = mutableListOf<WsV2ArrivalsResponse.Arrival.BlockPosition>()

            // Take resultSet regardless of response code?
            response.resultSet.arrival?.forEach { arrival ->
                val isValid = if (isStreetCar) {
                    arrival.fullSign.contains("streetcar", ignoreCase = true)
                } else {
                    arrival.fullSign.contains("MAX", ignoreCase = true)
                            || arrival.fullSign.contains("WES", ignoreCase = true)
                }
                if (!isValid) return@forEach

                arrival.blockPosition?.let { bp ->
                    blockPositions.add(bp)
                }

                arrivalRows.add(
                    ArrivalRow(
                        id = arrival.id,
                        feet = arrival.feet?.toLong() ?: 0L,
                        inCongestion = arrival.inCongestion?.boolToLong(),
                        departed = arrival.departed?.boolToLong(),
                        scheduled = arrival.scheduled,
                        loadPercentage = arrival.loadPercentage?.toLong(),
                        shortSign = arrival.shortSign,
                        estimated = arrival.estimated,
                        detoured = arrival.detoured.boolToLong(),
                        tripId = arrival.tripId,
                        dir = arrival.dir.toLong(),
                        blockId = arrival.blockID,
                        route = arrival.route.toLong(),
                        piece = arrival.piece,
                        fullSign = arrival.fullSign,
                        dropOffOnly = arrival.dropOffOnly?.boolToLong(),
                        vehicleId = arrival.vehicleID,
                        showMilesAway = arrival.showMilesAway?.boolToLong(),
                        locid = arrival.locid,
                        newTrip = arrival.newTrip.boolToLong(),
                        status = arrival.status,
                        blockPositionId = arrival.blockPosition?.id,
                    )
                )
            }

            appDatabase.transaction {
                appDatabase.arrivalQueries.deleteByLocIds(locIds)
                blockPositions.forEach { bp ->
                    appDatabase.blockPositionQueries.insertOrReplace(
                        id = bp.id,
                        route_number = bp.routeNumber.toLong(),
                        sign_message = bp.signMessage,
                        heading = bp.heading.toLong(),
                        next_stop_seq = bp.nextStopSeq.toLong(),
                        trip_id = bp.tripID,
                        at = bp.at,
                        sign_message_long = bp.signMessageLong,
                        last_loc_id = bp.lastLocID,
                        next_loc_id = bp.nextLocID,
                        last_stop_seq = bp.lastStopSeq?.toLong(),
                        vehicle_id = bp.vehicleID?.toLong(),
                        new_trip = bp.newTrip.boolToLong(),
                        direction = bp.direction.toLong(),
                        lat = bp.lat,
                        lng = bp.lng,
                    )
                }
                arrivalRows.forEach { row ->
                    appDatabase.arrivalQueries.insertIgnore(
                        id = row.id,
                        feet = row.feet,
                        in_congestion = row.inCongestion,
                        departed = row.departed,
                        scheduled = row.scheduled,
                        load_percentage = row.loadPercentage,
                        short_sign = row.shortSign,
                        estimated = row.estimated,
                        detoured = row.detoured,
                        trip_id = row.tripId,
                        dir = row.dir,
                        block_id = row.blockId,
                        route = row.route,
                        piece = row.piece,
                        full_sign = row.fullSign,
                        drop_off_only = row.dropOffOnly,
                        vehicle_id = row.vehicleId,
                        show_miles_away = row.showMilesAway,
                        locid = row.locid,
                        new_trip = row.newTrip,
                        status = row.status,
                        block_position_id = row.blockPositionId,
                    )
                }
            }

            settings.putLong(settingsKey, Clock.System.now().toEpochMilliseconds())
        } catch (_: Exception) {
            return false
        }

        return true
    }

    companion object {
        private const val THROTTLE_LOCID = 86400000L // 24 hours
        const val THROTTLE_GET_ARRIVALS_S = 10L // arrivals polling interval (10 seconds)
        const val EXPIRE_ARRIVALS = 300000L // 5 minutes
        const val DELIVERY_DEBOUNCE_MS = 600L
    }
}

private fun Boolean.boolToLong() = if (this) 1L else 0L
