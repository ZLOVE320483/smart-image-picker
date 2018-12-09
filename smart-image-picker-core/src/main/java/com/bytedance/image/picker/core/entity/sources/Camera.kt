package com.bytedance.image.picker.core.entity.sources

import androidx.annotation.IdRes
import com.bytedance.image.picker.core.ui.camera.SystemCameraPickerView
import kotlin.reflect.KClass

@Retention
@Target(
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER
)
annotation class Camera(

        val componentClazz: KClass<*> = SystemCameraPickerView::class,

        val openAsFragment: Boolean = true,

        @IdRes val containerViewId: Int = 0
)