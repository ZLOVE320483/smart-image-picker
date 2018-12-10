package com.bytedance.image.picker.support.model

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import com.bytedance.image.picker.support.loader.AlbumLoader
import java.lang.ref.WeakReference

class AlbumCollection: androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var mContext: WeakReference<Context>
    private lateinit var mLoaderManager: androidx.loader.app.LoaderManager
    private var mCallbacks: AlbumCallbacks? = null
    var currentSelection: Int = 0
        private set

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<Cursor> {
        return AlbumLoader.newInstance(mContext.get())
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, data: Cursor) {
        mCallbacks?.onAlbumLoad(data)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        mCallbacks?.onAlbumReset()
    }

    fun onCreate(activity: androidx.fragment.app.FragmentActivity, callbacks: AlbumCallbacks) {
        mContext = WeakReference(activity)
        mLoaderManager = activity.supportLoaderManager
        mCallbacks = callbacks
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }

        currentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_SELECTION, currentSelection)
    }

    fun onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID)
        mCallbacks = null
    }

    fun loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this)
    }

    fun setStateCurrentSelection(currentSelection: Int) {
        this.currentSelection = currentSelection
    }

    interface AlbumCallbacks {
        fun onAlbumLoad(cursor: Cursor)

        fun onAlbumReset()
    }

    companion object {
        private const val LOADER_ID = 1
        private const val STATE_CURRENT_SELECTION = "state_current_selection"
    }
}