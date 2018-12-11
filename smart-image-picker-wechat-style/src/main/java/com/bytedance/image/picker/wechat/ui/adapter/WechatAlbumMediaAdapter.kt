package com.bytedance.image.picker.wechat.ui.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.image.picker.support.model.SelectedItemCollection
import com.bytedance.image.picker.support.ui.adapter.AlbumMediaAdapter
import com.bytedance.image.picker.wechat.R

class WechatAlbumMediaAdapter(context: Context,
                              selectedCollection: SelectedItemCollection,
                              recyclerView: RecyclerView)
    : AlbumMediaAdapter(context, selectedCollection, recyclerView) {

    override val itemLayoutRes: Int
        get() = R.layout.wechat_media_grid_item
}
