package com.hzlgrn.pdxrail.viewmodel.railsystem

import com.google.android.gms.maps.model.LatLng

sealed class RailSystemMapItem {
    sealed class Line : RailSystemMapItem() {
        abstract val polyline: ArrayList<LatLng>

        data class Basic(override val polyline: ArrayList<LatLng>) : Line()

        data class MaxBlue(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxGreen(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxOrange(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxRed(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxYellow(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxBlueGreen(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxBlueRed(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxGreenOrange(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxGreenYellow(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxBlueGreenRed(override val polyline: ArrayList<LatLng>) : Line()
        data class MaxBlueGreenRedYellow(override val polyline: ArrayList<LatLng>) : Line()
        data class WES(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarALoop(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarBLoop(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarNorthSouth(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarAB(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarNSB(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarNSA(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarMaxABOrange(override val polyline: ArrayList<LatLng>) : Line()
        data class StreetcarNSAB(override val polyline: ArrayList<LatLng>) : Line()
    }

    sealed class Marker() : RailSystemMapItem() {
        data class MarkerId(val uniqueIdString: String)

        /***
         * Every marker has it's place
         */
        abstract val position: LatLng

        data class Undefined(override val position: LatLng) : Marker()

        /***
         * A Stop marker should be clickable so every Stop needs a unique MarkerId and some way of
         * describing itself.
         *
         * Caution: stationText is not localized?
         * .
         */
        sealed class Stop() : Marker() {
            abstract val uniqueId: MarkerId
            abstract val stationText: String?

            data class MaxStop(
                override val position: LatLng,
                override val uniqueId: MarkerId,
                override val stationText: String?,
            ) : Stop()

            data class StreetcarStop(
                override val position: LatLng,
                override val uniqueId: MarkerId,
                override val stationText: String?,
            ) : Stop()
        }

        sealed class Arrival() : Marker() {
            data class Default(override val position: LatLng, val heading: Int): Arrival()
            data class MaxBlue(override val position: LatLng, val heading: Int): Arrival()
            data class MaxGreen(override val position: LatLng, val heading: Int): Arrival()
            data class MaxOrange(override val position: LatLng, val heading: Int): Arrival()
            data class MaxRed(override val position: LatLng, val heading: Int): Arrival()
            data class MaxYellow(override val position: LatLng, val heading: Int): Arrival()
            data class NSLine(override val position: LatLng, val heading: Int): Arrival()
            data class ALoop(override val position: LatLng, val heading: Int): Arrival()
            data class BLoop(override val position: LatLng, val heading: Int): Arrival()
        }
    }
}