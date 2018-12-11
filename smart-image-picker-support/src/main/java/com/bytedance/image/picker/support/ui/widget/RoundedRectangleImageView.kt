package com.bytedance.image.picker.support.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class RoundedRectangleImageView: AppCompatImageView {

    private var mRadius: Float = 0.toFloat() // dp
    private var mRoundedRectPath: Path? = null
    private var mRectF: RectF? = null

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        val density = context.resources.displayMetrics.density
        mRadius = 2.0f * density
        mRoundedRectPath = Path()
        mRectF = RectF()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mRectF!!.set(0.0f, 0.0f, width.toFloat(), height.toFloat())
        mRoundedRectPath!!.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.clipPath(mRoundedRectPath!!)
        super.onDraw(canvas)
    }
}