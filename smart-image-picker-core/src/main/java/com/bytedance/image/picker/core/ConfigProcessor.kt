package com.bytedance.image.picker.core

import com.bytedance.image.picker.core.entity.ConfigProvider
import com.bytedance.image.picker.core.entity.sources.SourcesFrom
import com.bytedance.image.picker.core.scheduler.IRxImagePickerSchedulers
import com.bytedance.image.picker.core.ui.ActivityPickerViewController
import io.reactivex.Observable

class ConfigProcessor(private val schedulers: IRxImagePickerSchedulers) {

    fun process(configProvider: ConfigProvider): Observable<*> {
        return Observable.just(0)
                .flatMap {
                    if (!configProvider.openAsFragment) {
                        return@flatMap ActivityPickerViewController.instance.pickImage()
                    }
                    when(configProvider.sourceForm) {
                        SourcesFrom.GALLERY,
                        SourcesFrom.CAMERA -> configProvider.pickerView.pickImage()
                    }
                }
                .subscribeOn(schedulers.io())
                .observeOn(schedulers.ui())
    }
}