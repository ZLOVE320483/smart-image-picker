package com.bytedance.image.picker.wechat.ui.widget

import android.content.Context
import androidx.appcompat.widget.ListPopupWindow
import com.bytedance.image.picker.support.ui.widget.AlbumsSpinner

class WechatAlbumsSpinner(context: Context) : AlbumsSpinner() {

    init {
        mListPopupWindow = ListPopupWindow(context)
        mListPopupWindow.isModal = true

        mListPopupWindow.setContentWidth(context.resources.displayMetrics.widthPixels)
        mListPopupWindow.setOnItemClickListener { parent, view, position, id ->
            this@WechatAlbumsSpinner.onItemSelected(parent.context, position)
            mOnItemSelectedListener?.onItemSelected(parent, view, position, id)
        }
    }
}