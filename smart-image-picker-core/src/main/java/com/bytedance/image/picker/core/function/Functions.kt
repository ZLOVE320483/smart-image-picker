package com.bytedance.image.picker.core.function

import android.net.Uri
import com.bytedance.image.picker.core.entity.Result

fun parseResultNoExtraData(uri: Uri): Result {
    return Result.Builder(uri).build()
}