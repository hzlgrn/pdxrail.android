package com.hzlgrn.pdxrail.data.model

data class ArrivalItemData(
    val scheduled: Long,
    val estimated: Long?,
    val shortSign: String?,
    val lat: Double,
    val lon: Double,
    val heading: Float,
)
