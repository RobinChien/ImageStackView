package com.robinchien.imagestackview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

class ImageStackView @JvmOverloads constructor(
    val ctx: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : LinearLayout(ctx, attrs, defStyleAttr) {

    // Properties
    private val MAX_NUM_VIEWS: Int = 5
    private val NUM_VIEWS_IN_FIRST_STACK = 1
    private var images: MutableList<Drawable> = mutableListOf()

    //region Draw Method
    fun addImage(image: Drawable, index: Int? = null) {
        if (index != null) {
            images.add(index, image)
        } else {
            images.add(image)
        }

        updateViews()
    }

    fun addImages(images: List<Drawable>) {
        this.images.addAll(images)
        updateViews()
    }

    fun removeImage(imageView: ImageView) {
        val index = images.indexOf(imageView)
        if (index < 0) {
            return
        } else {
            images.removeAt(index = index)
        }

        updateViews()
    }

    private fun updateViews() {
        removeAllViewsInLayout()

        if (images.isNullOrEmpty()) {
            return
        }

        val imageViews = images.map { it.toImageView() }
        var groupViews: MutableList<List<ImageView>> = mutableListOf()

        if (imageViews.size < MAX_NUM_VIEWS) {
            groupViews =
                if (imageViews.size <= NUM_VIEWS_IN_FIRST_STACK) {
                    mutableListOf(imageViews)
                } else {
                    mutableListOf(
                        imageViews.subList(0, NUM_VIEWS_IN_FIRST_STACK),
                        imageViews.subList(NUM_VIEWS_IN_FIRST_STACK, images.size)
                    )
                }

        } else {
            val diffNumViews = images.size - MAX_NUM_VIEWS

            if (diffNumViews > 0) {
                addBlackOverlay(image = images.get(diffNumViews - 1), "+ $diffNumViews")
            }

            val numViewsInGroup: Int = MAX_NUM_VIEWS.plus(1).div(2)
            groupViews = mutableListOf(
                imageViews.subList(0, numViewsInGroup),
                imageViews.subList(numViewsInGroup, MAX_NUM_VIEWS)
            )
        }

        val subStackViewOrientation: Int =
            if (images.size < MAX_NUM_VIEWS) {
                VERTICAL
            } else {
                HORIZONTAL
            }

        groupViews
            .map { groupView ->
                newStackView(
                    groupView = groupView,
                    orientation = subStackViewOrientation
                )
            }
            .run {
                newStackView(
                    groupView = this,
                    orientation = if (subStackViewOrientation == HORIZONTAL) VERTICAL else HORIZONTAL
                )
            }
            .apply {
                addViewInLayout(
                    this,
                    -1,
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                )
            }
    }

    private fun newStackView(groupView: List<View>, orientation: Int): LinearLayout {
        val linearLayout = LinearLayout(ctx).apply {
            this.orientation = orientation
            this.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                1f
            )
        }
        groupView.forEach { view ->
            linearLayout.addView(view)
        }

        return linearLayout
    }

    private fun addBlackOverlay(image: Drawable, text: String) {
        if (text.isEmpty()) {
            return
        }

        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            this.alignWithParent = true
        }

        val relativeLayout: RelativeLayout = RelativeLayout(ctx).apply {
            this.layoutParams = layoutParams
        }

        val textView: TextView = TextView(ctx).apply {
            this.layoutParams = layoutParams
            this.text = text
            this.setTextColor(Color.parseColor("#FFFFFF"))
            this.textSize = spToPx(14)
            this.gravity = Gravity.CENTER
        }

        val imageView: ImageView = ImageView(ctx).apply {
            this.setColorFilter(Color.parseColor("#66000000"))
            this.scaleType = ImageView.ScaleType.CENTER_CROP
            this.setImageDrawable(image)
        }

        relativeLayout.addView(imageView)
        relativeLayout.addView(textView)
    }
    // endregion

    private fun Drawable.toImageView(): ImageView {
        return ImageView(ctx).apply {
            this.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                1f
            )
            this.scaleType = ImageView.ScaleType.CENTER_CROP
            this.setImageDrawable(this@toImageView)
        }
    }

    private fun spToPx(sp: Int): Float {
        return sp * resources.displayMetrics.scaledDensity
    }
}