package com.hzlgrn.pdxrail.data.net.railsystem

data class WsV1StopsResponse(val resultSet: ResultSet) {
    data class ResultSet(
        var queryTime: String, // ex: "2019-08-27T09:45:53.824-0700"
        var location: List<Location>?)
    {
        data class Location(
            var lng: Double,
            var dir: String,
            var lat: Double,
            var locid: Long,
            var desc: String?)
    }

}



