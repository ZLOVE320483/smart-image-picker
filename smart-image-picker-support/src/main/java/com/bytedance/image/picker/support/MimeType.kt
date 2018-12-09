package com.bytedance.image.picker.support

import android.content.ContentResolver
import android.net.Uri
import android.text.TextUtils
import android.webkit.MimeTypeMap
import com.bytedance.image.picker.support.utils.PhotoMetadataUtils
import java.io.Serializable
import java.util.*

enum class MimeType constructor(private val mMimeTypeName: String,
                                private val mExtensions: Set<String>) {
    // ============== images ==============
    JPEG("image/jpeg", setOf(
            "jpg",
            "jpeg"
    )),
    PNG("image/png", setOf(
            "png"
    )),
    GIF("image/gif", setOf(
            "gif"
    )),
    BMP("image/x-ms-bmp", setOf(
            "bmp"
    )),
    WEBP("image/webp", setOf(
            "webp"
    )),

    // ============== videos ==============
    MPEG("video/mpeg", setOf(
            "mpeg",
            "mpg"
    )),
    MP4("video/mp4", setOf(
            "mp4",
            "m4v"
    )),
    QUICKTIME("video/quicktime", setOf(
            "mov"
    )),
    THREEGPP("video/3gpp", setOf(
            "3gp",
            "3gpp"
    )),
    THREEGPP2("video/3gpp2", setOf(
            "3g2",
            "3gpp2"
    )),
    MKV("video/x-matroska", setOf(
            "mkv"
    )),
    WEBM("video/webm", setOf(
            "webm"
    )),
    TS("video/mp2ts", setOf(
            "ts"
    )),
    AVI("video/avi", setOf(
            "avi"
    ));

    override fun toString(): String {
        return mMimeTypeName
    }

    fun checkType(resolver: ContentResolver, uri: Uri?): Boolean {
        val map = MimeTypeMap.getSingleton()
        if (uri == null) {
            return false
        }
        val type = map.getExtensionFromMimeType(resolver.getType(uri))
        var path: String? = null
        // lazy load the path and prevent resolve for multiple times
        var pathParsed = false
        for (extension in mExtensions) {
            if (extension == type) {
                return true
            }
            if (!pathParsed) {
                // we only resolve the path for one time
                path = PhotoMetadataUtils.getPath(resolver, uri)
                if (!TextUtils.isEmpty(path)) {
                    path = path!!.toLowerCase(Locale.US)
                }
                pathParsed = true
            }
            if (path != null && path.endsWith(extension)) {
                return true
            }
        }
        return false
    }

    companion object INSTANCE: Serializable {

        fun ofAll(): Set<MimeType> {
            return EnumSet.allOf(MimeType::class.java)
        }

        fun of(type: MimeType, vararg rest: MimeType): Set<MimeType> {
            return EnumSet.of(type, *rest)
        }

        fun ofImage(): Set<MimeType> {
            return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP)
        }

        fun ofVideo(): Set<MimeType> {
            return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI)
        }
    }
}