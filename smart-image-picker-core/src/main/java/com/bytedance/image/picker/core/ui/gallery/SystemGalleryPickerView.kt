package com.bytedance.image.picker.core.ui.gallery

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.fragment.app.FragmentActivity
import com.bytedance.image.picker.core.entity.Result
import com.bytedance.image.picker.core.ui.BaseSystemPickerView
import com.bytedance.image.picker.core.ui.ICustomPickerConfiguration
import com.bytedance.image.picker.core.ui.IGalleryCustomPickerView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SystemGalleryPickerView: BaseSystemPickerView(), IGalleryCustomPickerView {

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

        val pictureChooseIntent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            pictureChooseIntent = Intent(Intent.ACTION_PICK)
            pictureChooseIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        } else {
            pictureChooseIntent = Intent(Intent.ACTION_GET_CONTENT)
        }
        pictureChooseIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
        pictureChooseIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        pictureChooseIntent.type = "image/*"

        startActivityForResult(pictureChooseIntent, BaseSystemPickerView.GALLERY_REQUEST_CODE)
    }

    override fun getActivityResultUri(data: Intent?): Uri? {
        return data?.data
    }

}