package com.bytedance.image.picker.support.utils

import android.content.ContentResolver
import android.content.ContentResolver.SCHEME_CONTENT
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore

class PhotoMetadataUtils private constructor() {

    companion object {

        fun getPath(resolver: ContentResolver, uri: Uri?): String? {
            if (uri == null) {
                return null
            }

            if (SCHEME_CONTENT == uri.scheme) {
                var cursor: Cursor? = null
                try {
                    cursor = resolver.query(uri, arrayOf(MediaStore.Images.ImageColumns.DATA), null, null, null)
                    return if (cursor == null || !cursor.moveToFirst()) {
                        null
                    } else cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                } finally {
                    cursor?.close()
                }
            }
            return uri.path
        }
    }
}