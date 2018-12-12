package com.bytedance.image.picker.zhihu.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bytedance.image.picker.core.ui.ActivityPickerViewController
import com.bytedance.image.picker.support.entity.SelectionSpec
import com.bytedance.image.picker.zhihu.R

class ZhihuImagePickerActivity : AppCompatActivity() {

    private val fragment: ZhihuImagePickerFragment = ZhihuImagePickerFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(SelectionSpec.instance.themeId)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_zhihu)

        requestPermissionAndDisplayGallery()
    }

    private fun requestPermissionAndDisplayGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 99)
        } else {
            displayPickerView()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            displayPickerView()
        } else {
            closure()
        }
    }

    private fun displayPickerView() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_container, fragment)
                .commitAllowingStateLoss()

        fragment.pickImage()
                .subscribe({
                    ActivityPickerViewController.instance.emitResult(it)
                }, {
                    ActivityPickerViewController.instance.emitError(it)
                }, {
                    closure()
                })
    }

    fun closure() {
        ActivityPickerViewController.instance.endResultEmitAndReset()
        finish()
    }

    override fun onBackPressed() {
        closure()
    }

    companion object {

        const val REQUEST_CODE_PREVIEW = 23
    }

}
