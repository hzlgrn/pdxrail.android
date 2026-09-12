package com.hzlgrn.pdxrail.data.net

import com.hzlgrn.pdxrail.data.model.net.WsV1StopsResponse
import com.hzlgrn.pdxrail.data.model.net.WsV2ArrivalsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class PdxRailSystemClient(
    private val httpClient: HttpClient,
    baseUrl: String,
    private val apiKey: String,
) {

    /**
     * Base URL normalized to always end with a single trailing slash so endpoints
     * can be appended uniformly, no matter how each platform supplies the value
     * (Android BuildConfig vs iOS Info.plist).
     */
    private val baseUrl: String = baseUrl.trim().removeSuffix("/") + "/"

    open suspend fun wsV1Stops(
        radiusInFeet: Long,
        lat: Double,
        lon: Double,
        isStreetCar: Boolean,
    ): WsV1StopsResponse {
        return httpClient.get("${baseUrl}ws/V1/stops") {
            parameter("appID", apiKey)
            parameter("json", "true")
            parameter("feet", radiusInFeet)
            parameter("ll", "$lat, $lon")
            parameter("streetcar", isStreetCar)
        }.body()
    }

    open suspend fun wsV2Arrivals(
        csvLocId: String,
        isStreetCar: Boolean,
    ): WsV2ArrivalsResponse {
        return httpClient.get("${baseUrl}ws/V2/arrivals") {
            parameter("appID", apiKey)
            parameter("json", "true")
            parameter("showPosition", "true")
            parameter("locIDs", csvLocId)
            parameter("streetcar", isStreetCar)
        }.body()
    }
}
