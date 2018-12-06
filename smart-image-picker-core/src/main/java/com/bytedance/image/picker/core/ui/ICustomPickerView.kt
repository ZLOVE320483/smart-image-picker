package com.bytedance.image.picker.core.ui

import androidx.annotation.IdRes
import com.bytedance.image.picker.core.entity.Result
import io.reactivex.Observable

interface ICustomPickerView {

    fun display(fragmentActivity: androidx.fragment.app.FragmentActivity,
                @IdRes viewContainer: Int,
                configuration: ICustomPickerConfiguration?)

    fun pickImage(): Observable<Result>
}