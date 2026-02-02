package com.hzlgrn.pdxrail.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.hzlgrn.pdxrail.R


class DrawerIconView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0): View(context, attrs, defStyleAttr) {

    private val selectorPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private val vectorForeground by lazy {
        VectorDrawableCompat.create(context.resources, R.drawable.ic_launcher_foreground, context.theme)
    }
    private val vectorBackground by lazy {
        VectorDrawableCompat.create(context.resources, R.drawable.ic_launcher_background, context.theme)
    }
    private var shouldRefreshMask = true
    private var bitmapMask: Bitmap? = null
    private val vectorMask by lazy {
        VectorDrawableCompat.create(context.resources, R.drawable.mask_oregon, context.theme)
    }
    private var resultBitmap: Bitmap? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        shouldRefreshMask = true
        setMeasuredDimension(widthSize, heightSize)
        resultBitmap?.recycle()
        resultBitmap = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas?.run {
            if (resultBitmap == null && width > 0 && height > 0) {
                resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                resultBitmap?.let { bitmap ->
                    val resultCanvas = Canvas(bitmap)
                    vectorBackground?.setBounds(0, 0, width, height)
                    vectorBackground?.draw(resultCanvas)
                    vectorForeground?.setBounds(0, 0, width, height)
                    vectorForeground?.draw(resultCanvas)
                    if (shouldRefreshMask && width > 0 && height > 0) {
                        vectorMask?.let { vector ->
                            bitmapMask?.recycle()
                            bitmapMask = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
                                val maskCanvas = Canvas(it)
                                vector.setBounds(0, 0, width, height)
                                vector.draw(maskCanvas)
                                shouldRefreshMask = false
                            }
                        }
                    }
                    bitmapMask?.let { resultCanvas.drawBitmap(it, 0f, 0f, selectorPaint) }
                }
            }
            resultBitmap?.let { drawBitmap(it, 0f, 0f, null) }
        }
    }

}