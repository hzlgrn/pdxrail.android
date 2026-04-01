package com.hzlgrn.pdxrail.data.model.net

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsV2ArrivalsResponse(
    @SerialName("resultSet") val resultSet: ResultSet,
) {
    @Serializable
    data class ResultSet(
        @SerialName("arrival") val arrival: List<Arrival>? = null,
        @SerialName("queryTime") val queryTime: Long,
        @SerialName("location") val location: List<Location> = emptyList(),
    )

    @Serializable
    data class Arrival(
        @SerialName("feet") val feet: Int? = null,
        @SerialName("inCongestion") val inCongestion: Boolean? = null,
        @SerialName("departed") val departed: Boolean? = null,
        @SerialName("scheduled") val scheduled: Long,
        @SerialName("loadPercentage") val loadPercentage: Int? = null,
        @SerialName("shortSign") val shortSign: String? = null,
        @SerialName("blockPosition") val blockPosition: BlockPosition? = null,
        @SerialName("estimated") val estimated: Long? = null,
        @SerialName("detoured") val detoured: Boolean,
        @SerialName("tripID") val tripId: String? = null,
        @SerialName("dir") val dir: Int,
        @SerialName("blockID") val blockID: Long,
        @SerialName("route") val route: Int,
        @SerialName("piece") val piece: String? = null,
        @SerialName("fullSign") val fullSign: String,
        @SerialName("dropOffOnly") val dropOffOnly: Boolean? = null,
        @SerialName("vehicleID") val vehicleID: String? = null,
        @SerialName("showMilesAway") val showMilesAway: Boolean? = null,
        @SerialName("id") val id: String,
        @SerialName("locid") val locid: Long,
        @SerialName("newTrip") val newTrip: Boolean,
        @SerialName("status") val status: String,
    ) {
        @Serializable
        data class BlockPosition(
            @SerialName("routeNumber") val routeNumber: Int,
            @SerialName("signMessage") val signMessage: String? = null,
            @SerialName("lng") val lng: Double,
            @SerialName("heading") val heading: Int,
            @SerialName("nextStopSeq") val nextStopSeq: Int,
            @SerialName("tripID") val tripID: String,
            @SerialName("at") val at: Long,
            @SerialName("signMessageLong") val signMessageLong: String? = null,
            @SerialName("lastLocID") val lastLocID: Long? = null,
            @SerialName("nextLocID") val nextLocID: Long? = null,
            @SerialName("lastStopSeq") val lastStopSeq: Int? = null,
            @SerialName("id") val id: Long,
            @SerialName("vehicleID") val vehicleID: Int? = null,
            @SerialName("newTrip") val newTrip: Boolean,
            @SerialName("lat") val lat: Double,
            @SerialName("direction") val direction: Int,
            @SerialName("locid") val locid: Long? = null,
            @SerialName("status") val status: String? = null,
        )
    }

    @Serializable
    data class Location(
        @SerialName("lng") val lng: Double,
        @SerialName("dir") val dir: String,
        @SerialName("lat") val lat: Double,
        @SerialName("locid") val locid: Long? = null,
        @SerialName("desc") val desc: String? = null,
    )
}
