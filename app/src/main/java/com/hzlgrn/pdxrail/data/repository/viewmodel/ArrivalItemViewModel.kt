package com.hzlgrn.pdxrail.data.repository.viewmodel

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng

class ArrivalItemViewModel(
        val textShortSign: String,
        val textScheduled: String,
        val textEstimated: String,
        @ColorInt val colorTextEstimated: Int,
        @DrawableRes val drawableArrivalMarker: Int,
        val drawableRotation: Float,
        val latlng: LatLng? = null
)