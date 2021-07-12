package com.hzlgrn.pdxrail.data.net

import com.hzlgrn.pdxrail.Domain
import com.hzlgrn.pdxrail.data.net.railsystem.WsV1StopsResponse
import com.hzlgrn.pdxrail.data.net.railsystem.WsV2ArrivalsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RailSystemService {

    @GET(Domain.RailSystem.WsV2Arrivals)
    fun wsV2Arrivals(
        @Query("locIDs") csvLocId: String,
        @Query("streetcar") isStreetCar: Boolean): Call<WsV2ArrivalsResponse>

    @GET(Domain.RailSystem.WsV1Stops)
    fun wsV1Stops(
        @Query("feet") radiusInFeet: Long,
        @Query("ll") latlon: String,
        @Query("streetcar") isStreetCar: Boolean): Call<WsV1StopsResponse>

}