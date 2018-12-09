package com.bytedance.image.picker.core.ui

import android.content.Context
import com.bytedance.image.picker.core.entity.Result
import com.bytedance.image.picker.core.entity.sources.Camera
import com.bytedance.image.picker.core.entity.sources.Gallery
import io.reactivex.Observable

interface SystemImagePicker {

    @Gallery
    fun openGallery(context: Context): Observable<Result>

    @Camera
    fun openCamera(context: Context): Observable<Result>
}