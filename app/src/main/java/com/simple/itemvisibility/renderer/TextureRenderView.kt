package com.simple.itemvisibility.renderer

import android.content.Context
import android.util.AttributeSet
import android.util.Pair
import android.view.TextureView
import kotlin.math.abs

/**
 * Created by zh on 2023/4/20.
 */
class TextureRenderView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : TextureView(context, attrs) {

    private var _videoWidth: Int = 0
    private var _videoHeight: Int = 0
    private var _zoom: Boolean = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size: Pair<Int, Int> = resize(
            _videoWidth.toFloat(),
            _videoHeight.toFloat(),
            measuredWidth.toFloat(),
            measuredHeight.toFloat()
        )
        if (size.first == 0 || size.second == 0) return
        super.onMeasure(
            MeasureSpec.makeMeasureSpec(size.first, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(size.second, MeasureSpec.EXACTLY)
        )
    }

    fun setVideoSize(width: Int, height: Int, zoom: Boolean) {
        if (_videoWidth != width || _videoHeight != height) {
            _videoWidth = width
            _videoHeight = height
            _zoom = zoom
            requestLayout()
        }
    }

    private fun resize(
        videoWidth: Float,
        videoHeight: Float,
        measuredWidth: Float,
        measuredHeight: Float
    ): Pair<Int, Int> {
        if (videoWidth == 0.0F || videoHeight == 0.0F) return Pair.create(0, 0)

        var width: Float = measuredWidth
        var height: Float = measuredHeight
        val viewAspectRatio: Float = width / height
        val videoAspectRatio: Float = videoWidth / videoHeight
        val difference: Float = videoAspectRatio / viewAspectRatio - 1
        if (abs(difference) <= 0.01F) return Pair.create(0, 0)
        if (_zoom) {
            if (difference > 0) {
                width = height * videoAspectRatio
            } else {
                height = width / videoAspectRatio
            }
        } else {
            if (difference > 0) {
                height = width / videoAspectRatio
            } else {
                width = height * videoAspectRatio
            }
        }
        return Pair.create(width.toInt(), height.toInt())
    }
}