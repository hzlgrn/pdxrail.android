package com.hzlgrn.pdxrail.viewmodel.bitmap

import android.graphics.Bitmap

sealed class MapIconBitmap {
    data object Idle: MapIconBitmap()
    data object Loading: MapIconBitmap()
    data class Display(val mapIconBitmap: Bitmap): MapIconBitmap()
}