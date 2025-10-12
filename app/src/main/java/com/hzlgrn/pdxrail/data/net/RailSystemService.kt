package com.hzlgrn.pdxrail.data.net

import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.net.railsystem.WsV1StopsResponse
import com.hzlgrn.pdxrail.data.net.railsystem.WsV2ArrivalsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RailSystemService {

    @GET(Domain.RailSystem.PATH_WS_V2_ARRIVALS)
    fun wsV2Arrivals(
        @Query("locIDs") csvLocId: String,
        @Query("streetcar") isStreetCar: Boolean
    ): Call<WsV2ArrivalsResponse>

    @GET(Domain.RailSystem.PATH_WS_V1_STOPS)
    fun wsV1Stops(
        @Query("feet") radiusInFeet: Long,
        @Query("ll") latlon: String,
        @Query("streetcar") isStreetCar: Boolean
    ): Call<WsV1StopsResponse>

}