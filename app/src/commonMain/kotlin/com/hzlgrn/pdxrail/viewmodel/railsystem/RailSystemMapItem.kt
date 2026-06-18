package com.hzlgrn.pdxrail.viewmodel.railsystem

import com.hzlgrn.pdxrail.data.geo.LatLon

sealed class RailSystemMapItem {
    sealed class Line : RailSystemMapItem() {
        abstract val polyline: List<LatLon>

        data class Basic(override val polyline: List<LatLon>) : Line()

        data class MaxBlue(override val polyline: List<LatLon>) : Line()
        data class MaxGreen(override val polyline: List<LatLon>) : Line()
        data class MaxOrange(override val polyline: List<LatLon>) : Line()
        data class MaxRed(override val polyline: List<LatLon>) : Line()
        data class MaxYellow(override val polyline: List<LatLon>) : Line()
        data class MaxBlueGreen(override val polyline: List<LatLon>) : Line()
        data class MaxBlueRed(override val polyline: List<LatLon>) : Line()
        data class MaxGreenOrange(override val polyline: List<LatLon>) : Line()
        data class MaxGreenYellow(override val polyline: List<LatLon>) : Line()
        data class MaxBlueGreenRed(override val polyline: List<LatLon>) : Line()
        data class MaxBlueGreenRedYellow(override val polyline: List<LatLon>) : Line()
        data class WES(override val polyline: List<LatLon>) : Line()
        data class StreetcarALoop(override val polyline: List<LatLon>) : Line()
        data class StreetcarBLoop(override val polyline: List<LatLon>) : Line()
        data class StreetcarNorthSouth(override val polyline: List<LatLon>) : Line()
        data class StreetcarAB(override val polyline: List<LatLon>) : Line()
        data class StreetcarNSB(override val polyline: List<LatLon>) : Line()
        data class StreetcarNSA(override val polyline: List<LatLon>) : Line()
        data class StreetcarMaxABOrange(override val polyline: List<LatLon>) : Line()
        data class StreetcarNSAB(override val polyline: List<LatLon>) : Line()
    }

    sealed class Marker : RailSystemMapItem() {
        data class MarkerId(val uniqueIdString: String)
        abstract val position: LatLon

        data class Undefined(override val position: LatLon) : Marker()

        sealed class Stop : Marker() {
            abstract val uniqueId: MarkerId
            abstract val stationText: String?

            data class MaxStop(
                override val position: LatLon,
                override val uniqueId: MarkerId,
                override val stationText: String?,
            ) : Stop()

            data class StreetcarStop(
                override val position: LatLon,
                override val uniqueId: MarkerId,
                override val stationText: String?,
            ) : Stop()

            data class CommuterStop(
                override val position: LatLon,
                override val uniqueId: MarkerId,
                override val stationText: String?,
            ) : Stop()
        }

        sealed class Arrival : Marker() {
            abstract val heading: Float
            data class Default(override val position: LatLon, override val heading: Float) : Arrival()
            data class MaxBlue(override val position: LatLon, override val heading: Float) : Arrival()
            data class MaxGreen(override val position: LatLon, override val heading: Float) : Arrival()
            data class MaxOrange(override val position: LatLon, override val heading: Float) : Arrival()
            data class MaxRed(override val position: LatLon, override val heading: Float) : Arrival()
            data class MaxYellow(override val position: LatLon, override val heading: Float) : Arrival()
            data class NSLine(override val position: LatLon, override val heading: Float) : Arrival()
            data class ALoop(override val position: LatLon, override val heading: Float) : Arrival()
            data class BLoop(override val position: LatLon, override val heading: Float) : Arrival()
            data class Commuter(override val position: LatLon, override val heading: Float) : Arrival()
        }
    }
}
