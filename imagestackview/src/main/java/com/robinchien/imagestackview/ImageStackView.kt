package com.robinchien.imagestackview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView

class ImageStackView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    // Properties
    private var countImage: Int = 0

    //region Draw Method
    private fun drawableToBitmap(drawable: Drawable?): Bitmap? =
        drawable?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && it is VectorDrawable) {
                it.vectorDrawableToBitmap()
            } else {
                when (it) {
                    is BitmapDrawable -> it.bitmapDrawableToBitmap()
                    else -> it.toBitmap()
                }
            }
        }

    private fun VectorDrawable.vectorDrawableToBitmap(): Bitmap {
        // Generate max bitmap size from view when is vector drawable
        // no when scale type is CENTER_INSIDE
        val bitmap = Bitmap.createBitmap(
            if (scaleType == ScaleType.CENTER_INSIDE) this.intrinsicWidth else width,
            if (scaleType == ScaleType.CENTER_INSIDE) this.intrinsicHeight else height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        this.setBounds(0, 0, canvas.width, canvas.height)
        this.draw(canvas)
        return bitmap
    }

    private fun BitmapDrawable.bitmapDrawableToBitmap(): Bitmap =
        bitmap.let {
            Bitmap.createScaledBitmap(
                it,
                this.intrinsicWidth,
                this.intrinsicHeight,
                false
            )
        }

    private fun Drawable.toBitmap(): Bitmap? =
        try {
            // Create Bitmap object out of the drawable
            val bitmap = Bitmap.createBitmap(
                this.intrinsicWidth,
                this.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            this.setBounds(0, 0, canvas.width, canvas.height)
            this.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    // endregion
}