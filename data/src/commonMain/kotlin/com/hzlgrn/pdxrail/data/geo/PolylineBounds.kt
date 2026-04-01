package com.hzlgrn.pdxrail.data.geo

data class PolylineBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
) {
    companion object {
        /**
         * This function assumes a polyline string format of "0.0,0.0 1.0,1.0"
         */
        fun calculatePolylineBounds(polylineString: String): PolylineBounds? {
            val points = polylineString.trim().split(" ")
            if (points.isEmpty()) return null

            var minLat = Double.MAX_VALUE
            var maxLat = -Double.MAX_VALUE
            var minLng = Double.MAX_VALUE
            var maxLng = -Double.MAX_VALUE

            for (point in points) {
                val coords = point.split(",")
                if (coords.size != 2) return null

                val lat = coords[0].toDoubleOrNull() ?: return null
                val lng = coords[1].toDoubleOrNull() ?: return null

                if (lat < minLat) minLat = lat
                if (lat > maxLat) maxLat = lat
                if (lng < minLng) minLng = lng
                if (lng > maxLng) maxLng = lng
            }

            return PolylineBounds(minLat, maxLat, minLng, maxLng)
        }
    }
}