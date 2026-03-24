package com.hzlgrn.pdxrail.data.model.net

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WsV1StopsResponse(
    @SerialName("resultSet") val resultSet: ResultSet,
) {
    @Serializable
    data class ResultSet(
        @SerialName("queryTime") val queryTime: Long,
        @SerialName("location") val location: List<Location>? = null,
    ) {
        @Serializable
        data class Location(
            @SerialName("lng") val lng: Double,
            @SerialName("dir") val dir: String,
            @SerialName("lat") val lat: Double,
            @SerialName("locid") val locid: Long,
            @SerialName("desc") val desc: String? = null,
        )
    }
}
