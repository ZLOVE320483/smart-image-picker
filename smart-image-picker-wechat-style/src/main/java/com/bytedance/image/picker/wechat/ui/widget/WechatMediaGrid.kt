package com.bytedance.image.picker.wechat.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.bytedance.image.picker.support.ui.widget.MediaGrid
import com.bytedance.image.picker.wechat.R

class WechatMediaGrid : MediaGrid {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    override fun getLayoutRes(): Int {
        return R.layout.wechat_media_grid_content
    }
}