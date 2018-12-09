package com.bytedance.image.picker.core

import android.app.Activity
import com.bytedance.image.picker.core.entity.ConfigProvider
import com.bytedance.image.picker.core.ui.ActivityPickerViewController
import com.bytedance.image.picker.core.ui.ICustomPickerConfiguration

class ImagePickerDisplayer(private val configProvider: ConfigProvider) {

    fun display() {
        configProvider.config?.onDisplay()

        if (!configProvider.openAsFragment)
            displayPickerViewAsActivity(configProvider.config)
        else
            displayPickerViewAsFragment(configProvider.config)
    }

    private fun displayPickerViewAsActivity(configuration: ICustomPickerConfiguration?) {
        val activityHolder = ActivityPickerViewController.instance
        activityHolder.setActivityClass(configProvider.componentClazz.java as Class<out Activity>)
        activityHolder.display(
                configProvider.fragmentActivity, configProvider.containerViewId, configuration
        )
    }

    private fun displayPickerViewAsFragment(configuration: ICustomPickerConfiguration?) {
        configProvider.pickerView.display(
                configProvider.fragmentActivity, configProvider.containerViewId, configuration
        )
    }
}