package com.hzlgrn.pdxrail.data.net.railsystem

data class WsV2ArrivalsResponse(var resultSet: ResultSet) {
    data class ResultSet(
            var arrival: List<Arrival>?,
            var queryTime: Long,
            var location: List<Location>)

    data class Arrival(
            var feet: Int?,
            var inCongestion: Boolean?,
            var departed: Boolean?,
            var scheduled: Long,
            var loadPercentage: Int?,
            var shortSign: String?,
            var blockPosition: BlockPosition?,
            var estimated: Long?,
            var detoured: Boolean,
            var tripId: String?,
            var dir: Int,
            var blockID: Long,
            var route: Int,
            var piece: String?,
            var fullSign: String,
            var dropOffOnly: Boolean?,
            var vehicleID: String?,
            var showMilesAway: Boolean?,
            var id: String,
            var locid: Long,
            var newTrip: Boolean,
            var status: String)
    {
        data class BlockPosition(
            var routeNumber: Int,
            var signMessage: String?,
            var lng: Double,
            var heading: Int,
            var nextStopSeq: Int,
            var tripID: String,
            var at: Long,
            var signMessageLong: String?,
            var lastLocID: Long?,
            var nextLocID: Long?,
            var lastStopSeq: Int?,
            var id: Long,
            var vehicleID: Int?,
            var newTrip: Boolean,
            var lat: Double,
            var direction: Int,
            var locid: Long?,
            var status: String?)
    }

    data class Location(
        var lng: Double,
        var dir: String,
        var lat: Double,
        var locid: Long?,
        var desc: String?)
}







