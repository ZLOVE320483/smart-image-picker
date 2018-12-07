package com.bytedance.image.picker.core.ui.camera

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import com.bytedance.image.picker.core.entity.Result
import com.bytedance.image.picker.core.ui.BaseSystemPickerView
import com.bytedance.image.picker.core.ui.ICameraCustomPickerView
import com.bytedance.image.picker.core.ui.ICustomPickerConfiguration
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.text.SimpleDateFormat
import java.util.*

class SystemCameraPickerView: BaseSystemPickerView(), ICameraCustomPickerView {

    private var cameraPictureUrl: Uri? = null

    override fun display(fragmentActivity: FragmentActivity, viewContainer: Int, configuration: ICustomPickerConfiguration?) {
        val fragmentManager = fragmentActivity.supportFragmentManager
        val fragment: androidx.fragment.app.Fragment? = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            val transaction = fragmentManager.beginTransaction()
            if (viewContainer != 0) {
                transaction.add(viewContainer, this, tag)
            } else {
                transaction.add(this, tag)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    override fun pickImage(): Observable<Result> {
        publishSubject = PublishSubject.create<Result>()
        return uriObserver
    }

    override fun startRequest() {
        if (!checkPermission()) {
            return
        }
        cameraPictureUrl = createImageUri()
        val pictureChooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPictureUrl)

        startActivityForResult(pictureChooseIntent, BaseSystemPickerView.CAMERA_REQUEST_CODE)
    }

    override fun getActivityResultUri(data: Intent?): Uri? {
        return cameraPictureUrl
    }

    private fun createImageUri(): Uri? {
        val contentResolver = activity!!.contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }
}