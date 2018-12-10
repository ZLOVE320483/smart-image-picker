package com.bytedance.image.picker.support.model

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import com.bytedance.image.picker.support.entity.Album
import com.bytedance.image.picker.support.loader.AlbumMediaLoader
import java.lang.ref.WeakReference

class AlbumMediaCollection: androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {
    private var mContext: WeakReference<Context>? = null
    private var mLoaderManager: androidx.loader.app.LoaderManager? = null
    private var mCallbacks: AlbumMediaCallbacks? = null

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<Cursor> {
        val context = mContext!!.get()
        val album = args!!.getParcelable<Album>(ARGS_ALBUM)

        return AlbumMediaLoader.newInstance(context, album,
                album.isAll && args.getBoolean(ARGS_ENABLE_CAPTURE, false))
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, data: Cursor) {
        mContext?.get() ?: return

        mCallbacks!!.onAlbumMediaLoad(data)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        mContext?.get() ?: return

        mCallbacks!!.onAlbumMediaReset()
    }

    fun onCreate(context: androidx.fragment.app.FragmentActivity, callbacks: AlbumMediaCallbacks) {
        mContext = WeakReference(context)
        mLoaderManager = context.supportLoaderManager
        mCallbacks = callbacks
    }

    fun onDestroy() {
        mLoaderManager!!.destroyLoader(LOADER_ID)
        mCallbacks = null
    }

    @JvmOverloads
    fun load(target: Album?, enableCapture: Boolean = false) {
        val args = Bundle()
        args.putParcelable(ARGS_ALBUM, target)
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture)
        mLoaderManager!!.initLoader(LOADER_ID, args, this)
    }

    interface AlbumMediaCallbacks {

        fun onAlbumMediaLoad(cursor: Cursor)

        fun onAlbumMediaReset()
    }

    companion object {
        private const val LOADER_ID = 2
        private const val ARGS_ALBUM = "args_album"
        private const val ARGS_ENABLE_CAPTURE = "args_enable_capture"
    }
}