package com.bytedance.image.picker.core.scheduler

import io.reactivex.Scheduler

interface IRxImagePickerSchedulers {

    fun ui(): Scheduler

    fun io(): Scheduler
}