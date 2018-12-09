package com.bytedance.image.picker.core.entity.sources

import androidx.annotation.IdRes
import com.bytedance.image.picker.core.ui.gallery.SystemGalleryPickerView
import kotlin.reflect.KClass

@Retention
@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
annotation class Gallery(
        val componentClazz: KClass<*> = SystemGalleryPickerView::class,

        val openAsFragment: Boolean = true,

        @IdRes val containerViewId: Int = 0
)