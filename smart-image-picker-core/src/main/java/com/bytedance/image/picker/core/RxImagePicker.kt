package com.bytedance.image.picker.core

import com.bytedance.image.picker.core.ui.SystemImagePicker
import java.lang.reflect.Proxy

object RxImagePicker {

    fun create(): SystemImagePicker {
        return create(SystemImagePicker::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> create(classProviders: Class<T>): T {
        val proxyProviders = ProxyProviders()

        return Proxy.newProxyInstance(
                classProviders.classLoader,
                arrayOf<Class<*>>(classProviders),
                proxyProviders) as T
    }
}