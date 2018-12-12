package com.bytedance.image.picker.zhihu.engine.impl

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.bytedance.image.picker.support.engine.ImageEngine

class ZhihuGlideEngine : ImageEngine {

    override fun loadThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView, uri: Uri) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.centerCropTransform()
                        .placeholder(placeholder))
                .into(imageView)
    }

    override fun loadGifThumbnail(context: Context, resize: Int, placeholder: Drawable, imageView: ImageView,
                                  uri: Uri) {
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(RequestOptions.centerCropTransform()
                        .placeholder(placeholder))
                .into(imageView)
    }

    override fun loadImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Glide.with(context)
                .load(uri)
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView)
    }

    override fun loadGifImage(context: Context, resizeX: Int, resizeY: Int, imageView: ImageView, uri: Uri) {
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(RequestOptions.priorityOf(Priority.HIGH))
                .into(imageView)
    }

    override fun supportAnimatedGif(): Boolean {
        return true
    }

}
