package com.bytedance.image.picker.core.entity

import androidx.annotation.IdRes
import com.bytedance.image.picker.core.entity.sources.SourcesFrom
import com.bytedance.image.picker.core.ui.ICustomPickerConfiguration
import com.bytedance.image.picker.core.ui.ICustomPickerView
import kotlin.reflect.KClass

data class ConfigProvider(val componentClazz: KClass<*>,
                          val openAsFragment: Boolean,
                          val sourceForm: SourcesFrom,
                          @param:IdRes val containerViewId: Int,
                          val fragmentActivity: androidx.fragment.app.FragmentActivity,
                          val pickerView: ICustomPickerView,
                          val config: ICustomPickerConfiguration?)