package com.robinchien.imagestackview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView

class ImageStackView(
    val ctx: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : AppCompatImageView(ctx, attrs, defStyleAttr) {

    // Properties
    private var countImage: Int = 0
    private var imageViews: MutableList<ImageView> = mutableListOf()

    //region Draw Method
    fun add(imageView: ImageView, position: Int? = null) {
        imageView.scaleType = ScaleType.FIT_XY

        if (position != null) {
            imageViews.add(position, imageView)
        } else {
            imageViews.add(imageView)
        }

        updateView()
    }

    fun remove(imageView: ImageView) {
        val index = imageViews.indexOf(imageView)
        if (index < 0) {
            return
        } else {
            imageViews.removeAt(index = index)
        }

        updateView()
    }

    private fun updateView() {
        if (imageViews.isNullOrEmpty()) {
            return
        }


    }

    private fun addBlackOverlay(imageView: ImageView, text: String) {
        if (text.isEmpty()) {
            return
        }

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            this.gravity = Gravity.CENTER
        }

        val frameLayout: FrameLayout = FrameLayout(ctx).apply {
            this.layoutParams = layoutParams
        }

        val textView: TextView = TextView(ctx).apply {
            this.layoutParams = layoutParams
            this.text = text
            this.gravity = Gravity.CENTER
        }

        imageView.apply {
            this.setColorFilter(Color.parseColor("#26000000"))
            this.scaleType = ImageView.ScaleType.FIT_XY
        }

        frameLayout.addView(textView)
        frameLayout.addView(imageView)
    }

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