package com.bytedance.image.picker.wechat.ui

import com.bytedance.image.picker.support.ui.SelectedPreviewActivity
import com.bytedance.image.picker.wechat.R

class WechatSelectedPreviewActivity : SelectedPreviewActivity() {

    override val layoutRes: Int
        get() = R.layout.wechat_activity_media_preview
}
