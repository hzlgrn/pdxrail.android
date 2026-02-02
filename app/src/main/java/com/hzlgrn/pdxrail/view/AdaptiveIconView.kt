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


class AdaptiveIconView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var bitmapAdaptiveIcon: Bitmap? = null
    private var bitmapMask: Bitmap? = null

    private val paintMask = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    private var shouldRefreshMask = true

    private var vectorBackground: VectorDrawableCompat? = null
    private var vectorForeground: VectorDrawableCompat? = null
    private var vectorMask: VectorDrawableCompat? = null

    init {
        if (attrs != null) {
            with (context.obtainStyledAttributes(attrs, R.styleable.AdaptiveIconView)) {
                val bg = if (hasValue(R.styleable.AdaptiveIconView_backgroundVector)) {
                    getResourceId(R.styleable.AdaptiveIconView_backgroundVector, 0).takeIf { it != 0 }
                } else null
                val fg = if (hasValue(R.styleable.AdaptiveIconView_foregroundVector)) {
                    getResourceId(R.styleable.AdaptiveIconView_foregroundVector, 0).takeIf { it != 0 }
                } else null
                val msk = if (hasValue(R.styleable.AdaptiveIconView_maskVector)) {
                    getResourceId(R.styleable.AdaptiveIconView_maskVector, 0).takeIf { it != 0 }
                } else null
                vectorBackground = bg?.let { VectorDrawableCompat.create(context.resources, it, context.theme) }
                vectorForeground = fg?.let { VectorDrawableCompat.create(context.resources, it, context.theme) }
                vectorMask = msk?.let { VectorDrawableCompat.create(context.resources, it, context.theme) }
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        bitmapAdaptiveIcon?.recycle()
        bitmapAdaptiveIcon = null
        shouldRefreshMask = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width == 0 || height == 0) return
        canvas?.run {
            if (bitmapAdaptiveIcon == null) {
                bitmapAdaptiveIcon = createAdaptiveIconBitmap()
            }
            bitmapAdaptiveIcon?.let { drawBitmap(it, 0f, 0f, null) }
        }
    }

    private fun createAdaptiveIconBitmap(): Bitmap? {
        if (width == 0 || height == 0) return null
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also { bitmap ->
            val canvas = Canvas(bitmap)
            vectorBackground?.setBounds(0, 0, width, height)
            vectorBackground?.draw(canvas)
            vectorForeground?.setBounds(0, 0, width, height)
            vectorForeground?.draw(canvas)
            if (shouldRefreshMask) {
                vectorMask?.let {
                    bitmapMask?.recycle()
                    bitmapMask = createMaskBitmap(it)
                    if (bitmapMask != null) shouldRefreshMask = false
                }
            }
            bitmapMask?.let { canvas.drawBitmap(it, 0f, 0f, paintMask) }
        }
    }

    private fun createMaskBitmap(vector: VectorDrawableCompat): Bitmap? {
        if (width == 0 || height == 0) return null
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).also {
            val maskCanvas = Canvas(it)
            vector.setBounds(0, 0, width, height)
            vector.draw(maskCanvas)
        }
    }

}