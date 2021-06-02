package com.robinchien.imagestackview

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView

class ImageStackView(
    val ctx: Context,
    val attrs: AttributeSet? = null,
    val defStyleAttr: Int = 0
) : AppCompatImageView(ctx, attrs, defStyleAttr) {

    // Properties
    private val MAX_NUM_VIEWS: Int = 5
    private val NUM_VIEWS_IN_FIRST_STACK = 1
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

        updateViews()
    }

    fun remove(imageView: ImageView) {
        val index = imageViews.indexOf(imageView)
        if (index < 0) {
            return
        } else {
            imageViews.removeAt(index = index)
        }

        updateViews()
    }

    private fun updateViews() {
        if (imageViews.isNullOrEmpty()) {
            return
        }

        var groupViews: MutableList<MutableList<ImageView>> = mutableListOf()

        if (imageViews.size < MAX_NUM_VIEWS) {
            groupViews =
                if (imageViews.size <= NUM_VIEWS_IN_FIRST_STACK) {
                    mutableListOf(imageViews)
                } else {
                    mutableListOf(
                        imageViews.subList(0, NUM_VIEWS_IN_FIRST_STACK),
                        imageViews.subList(NUM_VIEWS_IN_FIRST_STACK, imageViews.size)
                    )
                }

        } else {
            val diffNumViews = imageViews.size - MAX_NUM_VIEWS

            if (diffNumViews > 0) {
                addBlackOverlay(imageView = imageViews.get(diffNumViews - 1), "+ $diffNumViews")
            }

            val numViewsInGroup: Int = MAX_NUM_VIEWS.plus(1).div(2)
            groupViews = mutableListOf(
                imageViews.subList(0, numViewsInGroup),
                imageViews.subList(numViewsInGroup, MAX_NUM_VIEWS)
            )
        }

        val subStackViewOrientation: Int =
            if (imageViews.size < MAX_NUM_VIEWS) {
                LinearLayout.VERTICAL
            } else {
                LinearLayout.HORIZONTAL
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
                    orientation = if (subStackViewOrientation == LinearLayout.HORIZONTAL) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
                )
            }
            .apply {
                // TODO: 6/2/21 Add to parent view
            }
    }

    private fun newStackView(groupView: List<View>, orientation: Int): LinearLayout {
        val linearLayout = LinearLayout(ctx).apply {
            this.orientation = orientation
            this.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }
        groupView.forEach { view ->
            linearLayout.addView(view)
        }

        return linearLayout
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
    // endregion
}