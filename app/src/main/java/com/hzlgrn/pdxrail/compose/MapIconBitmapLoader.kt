package com.hzlgrn.pdxrail.compose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import com.hzlgrn.pdxrail.R
import com.hzlgrn.pdxrail.di.loader.BitmapLoader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MapIconBitmapLoader @Inject constructor(
    @ApplicationContext val context: Context,
): BitmapLoader {
    override fun load(): Bitmap {
        val foreground = ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground)!!
        val background = ContextCompat.getDrawable(context, R.drawable.ic_launcher_background)!!
        val mask = ContextCompat.getDrawable(context, R.drawable.mask_oregon)!!
        val bitmap = createBitmap(background.intrinsicWidth, background.intrinsicHeight)
        val canvas = Canvas(bitmap)
        background.setBounds(0, 0, canvas.width, canvas.height)
        background.draw(canvas)
        foreground.setBounds(0, 0, canvas.width, canvas.height)
        foreground.draw(canvas)
        val maskPaint = Paint()
        maskPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        canvas.drawBitmap(mask.toBitmap(), 0f, 0f, maskPaint)
        val mapIconWidth = context.resources.getDimensionPixelSize(R.dimen.map_icon_width)
        return bitmap.scale(mapIconWidth, mapIconWidth, false)
    }
}