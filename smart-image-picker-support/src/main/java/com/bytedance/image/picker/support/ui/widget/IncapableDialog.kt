package com.bytedance.image.picker.support.ui.widget

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.bytedance.image.picker.support.R

class IncapableDialog: androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments!!.getString(EXTRA_TITLE)
        val message = arguments!!.getString(EXTRA_MESSAGE)

        val builder = AlertDialog.Builder(activity!!)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message)
        }
        builder.setPositiveButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"

        fun newInstance(title: String, message: String): IncapableDialog {
            val dialog = IncapableDialog()
            val args = Bundle()
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }
}