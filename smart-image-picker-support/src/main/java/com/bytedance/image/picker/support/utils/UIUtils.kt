package com.bytedance.image.picker.support.utils

import android.content.Context

object UIUtils {

    fun spanCount(context: Context, gridExpectedSize: Int): Int {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val expected = screenWidth.toFloat() / gridExpectedSize.toFloat()
        var spanCount = Math.round(expected)
        if (spanCount == 0) {
            spanCount = 1
        }
        return spanCount
    }
}