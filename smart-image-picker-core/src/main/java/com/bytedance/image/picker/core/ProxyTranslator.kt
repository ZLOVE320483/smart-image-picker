package com.bytedance.image.picker.core

import android.app.Activity
import android.content.Context
import com.bytedance.image.picker.core.entity.ConfigProvider
import com.bytedance.image.picker.core.entity.sources.Camera
import com.bytedance.image.picker.core.entity.sources.Gallery
import com.bytedance.image.picker.core.entity.sources.SourcesFrom
import com.bytedance.image.picker.core.ui.ActivityPickerViewController
import com.bytedance.image.picker.core.ui.ICustomPickerConfiguration
import com.bytedance.image.picker.core.ui.ICustomPickerView
import java.lang.NullPointerException
import java.lang.reflect.Method
import kotlin.reflect.KClass

class ProxyTranslator {

    fun processMethod(method: Method, args: Array<Any>?): ConfigProvider {
        val sourcesFrom = streamSourcesFrom(method)
        val asFragment = asFragment(method, sourcesFrom)
        val componentClass = getComponentClass(method, sourcesFrom)

        val context = getObjectFromMethodParam(method, Context::class.java, args)
                ?: throw NullPointerException(method.name
                        + " requires just one instance of type: Context, but none.")
        val fragmentActivity = transformContextToFragmentActivity(context)
        val runtimeConfiguration = getObjectFromMethodParam(method, ICustomPickerConfiguration::class.java, args)

        val pickerView: ICustomPickerView = if (asFragment &&
                ICustomPickerView::class.java.isAssignableFrom(componentClass.java)) {
            componentClass.java.newInstance() as ICustomPickerView
        } else if (!asFragment &&
                Activity::class.java.isAssignableFrom(componentClass.java)) {
            ActivityPickerViewController.instance
        } else
            throw IllegalArgumentException("Configration Conflict! The ui component as Activity: ${!asFragment}," +
                    " the Class type is: ${componentClass.simpleName}")

        return ConfigProvider(
                componentClass,
                asFragment,
                sourcesFrom,
                if (asFragment) containerViewId(method, sourcesFrom) else -1,
                fragmentActivity,
                pickerView,
                runtimeConfiguration)
    }

    private fun asFragment(method: Method, sourcesFrom: SourcesFrom): Boolean {
        return when (sourcesFrom) {
            SourcesFrom.CAMERA -> {
                method.getAnnotation(Camera::class.java).openAsFragment
            }
            SourcesFrom.GALLERY -> {
                method.getAnnotation(Gallery::class.java).openAsFragment
            }
        }
    }

    private fun getComponentClass(method: Method, sourcesFrom: SourcesFrom): KClass<*> {
        return when (sourcesFrom) {
            SourcesFrom.CAMERA -> {
                method.getAnnotation(Camera::class.java).componentClazz
            }
            SourcesFrom.GALLERY -> {
                method.getAnnotation(Gallery::class.java).componentClazz
            }
        }
    }

    /**
     * Handle the annotation configuration of image source. This configuration is user-defined annotation
     * above the method [Camera] or [Gallery].
     *
     *
     * By default, if not image source annotation was configured, you will get an [IllegalArgumentException].
     *
     * @return [SourcesFrom.CAMERA] or [SourcesFrom.GALLERY]
     */
    private fun streamSourcesFrom(method: Method): SourcesFrom {
        val camera = method.getAnnotation(Camera::class.java) != null
        val gallery = method.getAnnotation(Gallery::class.java) != null
        return if (camera && !gallery) {
            SourcesFrom.CAMERA
        } else if (gallery && !camera) {
            SourcesFrom.GALLERY
        } else if (!camera) {
            throw IllegalArgumentException("Did you forget to add the @Galley or the @Camera annotation?")
        } else {
            throw IllegalArgumentException("You should not add two conflicting annotation to this method: @Galley and @Camera.")
        }
    }

    private fun containerViewId(method: Method, sourcesFrom: SourcesFrom): Int {
        return when (sourcesFrom) {
            SourcesFrom.CAMERA -> {
                method.getAnnotation(Camera::class.java).containerViewId
            }
            SourcesFrom.GALLERY -> {
                method.getAnnotation(Gallery::class.java).containerViewId
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> getObjectFromMethodParam(method: Method,
                                             expectedClass: Class<T>,
                                             objectsMethod: Array<Any>?): T? {
        var countSameObjectsType = 0
        var expectedObject: T? = null

        if (objectsMethod == null)
            throw NullPointerException(method.name
                    + " requires the Context as argument at least.")

        for (objectParam in objectsMethod) {
            if (expectedClass.isAssignableFrom(objectParam.javaClass)) {
                expectedObject = objectParam as T
                countSameObjectsType++
            }
        }

        if (countSameObjectsType > 1) {
            throw IllegalArgumentException(method.name
                    + " requires just one instance of type: ${expectedClass.simpleName}, but $countSameObjectsType.")
        }
        return expectedObject
    }

    private fun transformContextToFragmentActivity(context: Context): androidx.fragment.app.FragmentActivity {
        return context as? androidx.fragment.app.FragmentActivity
                ?: throw IllegalArgumentException("the context should be FragmentActivity.")
    }
}