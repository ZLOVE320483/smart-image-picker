package com.bytedance.image.picker.zhihu.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import com.bytedance.image.picker.core.entity.Result
import com.bytedance.image.picker.core.function.parseResultNoExtraData
import com.bytedance.image.picker.core.ui.ActivityPickerViewController
import com.bytedance.image.picker.core.ui.ICustomPickerConfiguration
import com.bytedance.image.picker.core.ui.IGalleryCustomPickerView
import com.bytedance.image.picker.support.entity.Album
import com.bytedance.image.picker.support.entity.Item
import com.bytedance.image.picker.support.entity.SelectionSpec
import com.bytedance.image.picker.support.model.AlbumCollection
import com.bytedance.image.picker.support.model.SelectedItemCollection
import com.bytedance.image.picker.support.ui.AlbumPreviewActivity
import com.bytedance.image.picker.support.ui.BasePreviewActivity
import com.bytedance.image.picker.support.ui.SelectedPreviewActivity
import com.bytedance.image.picker.support.ui.adapter.AlbumMediaAdapter
import com.bytedance.image.picker.support.ui.adapter.AlbumsAdapter
import com.bytedance.image.picker.support.ui.widget.AlbumsSpinner
import com.bytedance.image.picker.zhihu.R
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.*

class ZhihuImagePickerFragment : androidx.fragment.app.Fragment(), IGalleryCustomPickerView, AlbumCollection.AlbumCallbacks,
        AdapterView.OnItemSelectedListener, View.OnClickListener,
        ZhihuImageListGridFragment.SelectionProvider, AlbumMediaAdapter.OnMediaClickListener,
        AlbumMediaAdapter.CheckStateListener {

    private val mAlbumCollection = AlbumCollection()

    private lateinit var mAlbumsSpinner: AlbumsSpinner
    private lateinit var mAlbumsAdapter: AlbumsAdapter

    private var publishSubject: PublishSubject<Result> = PublishSubject.create()

    private lateinit var mSelectedCollection: SelectedItemCollection

    private lateinit var mButtonPreview: TextView
    private lateinit var mButtonApply: TextView
    private lateinit var mContainer: View
    private lateinit var mEmptyView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, SelectionSpec.instance.themeId)
        val localInflater = inflater
                .cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_picker_zhihu, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mSelectedCollection = SelectedItemCollection(context!!)

        mButtonPreview = view.findViewById(R.id.button_preview)
        mButtonApply = view.findViewById(R.id.button_apply)
        mButtonPreview.setOnClickListener(this)
        mButtonApply.setOnClickListener(this)
        mContainer = view.findViewById(R.id.container)
        mEmptyView = view.findViewById(R.id.empty_view)

        val mButtonBack = view.findViewById<ImageView>(R.id.button_back)
        mButtonBack.setOnClickListener(this)

        mSelectedCollection.onCreate(savedInstanceState)
        updateBottomToolbar()

        mAlbumsAdapter = AlbumsAdapter(context!!, null, false)
        mAlbumsSpinner = AlbumsSpinner(context!!)
        mAlbumsSpinner.setOnItemSelectedListener(this)
        mAlbumsSpinner.setSelectedTextView(view.findViewById(R.id.selected_album))
        mAlbumsSpinner.setPopupAnchorView(view.findViewById(R.id.toolbar))
        mAlbumsSpinner.setAdapter(mAlbumsAdapter)
        mAlbumCollection.onCreate(activity!!, this)
        mAlbumCollection.onRestoreInstanceState(savedInstanceState)
        mAlbumCollection.loadAlbums()
    }

    override fun display(fragmentActivity: androidx.fragment.app.FragmentActivity,
                         viewContainer: Int,
                         configuration: ICustomPickerConfiguration?) {
        val fragmentManager = fragmentActivity.supportFragmentManager
        val fragment = fragmentManager.findFragmentByTag(tag)
        if (fragment == null) {
            if (viewContainer != 0)
                fragmentManager.beginTransaction().add(viewContainer, this, tag).commit()
            else
                throw IllegalArgumentException(
                        "the viewContainer == 0, please configrate the containerViewId in the @Gallery annotation."
                )
        }
    }

    override fun pickImage(): Observable<Result> {
        publishSubject = PublishSubject.create()
        return publishSubject
    }

    private fun closure() {
        if (activity is ZhihuImagePickerActivity) {
            (activity as ZhihuImagePickerActivity).closure()
        } else {
            val fragmentManager = fragmentManager
            val fragmentTransaction = fragmentManager!!.beginTransaction()
            fragmentTransaction.remove(this)
            fragmentTransaction.commit()
        }
        SelectionSpec.instance.onFinished()
    }

    override fun onAlbumLoad(cursor: Cursor) {
        mAlbumsAdapter.swapCursor(cursor)
        // select default album.
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            cursor.moveToPosition(mAlbumCollection.currentSelection)
            mAlbumsSpinner.setSelection(context!!,
                    mAlbumCollection.currentSelection)
            val album = Album.valueOf(cursor)
            if (album.isAll) {
                album.addCaptureCount()
            }
            onAlbumSelected(album)
        }
    }

    override fun onAlbumReset() {
        mAlbumsAdapter.swapCursor(null)
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        mAlbumCollection.setStateCurrentSelection(position)
        mAlbumsAdapter.cursor.moveToPosition(position)
        val album = Album.valueOf(mAlbumsAdapter.cursor)
        if (album.isAll) {
            album.addCaptureCount()
        }
        onAlbumSelected(album)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    private fun updateBottomToolbar() {
        val selectedCount = mSelectedCollection.count()
        if (selectedCount == 0) {
            mButtonPreview.isEnabled = false
            mButtonApply.isEnabled = false
            mButtonApply.text = getString(R.string.button_apply_default)
        } else if (selectedCount == 1 && SelectionSpec.instance.singleSelectionModeEnabled()) {
            mButtonPreview.isEnabled = true
            mButtonApply.setText(R.string.button_apply_default)
            mButtonApply.isEnabled = true
        } else {
            mButtonPreview.isEnabled = true
            mButtonApply.isEnabled = true
            mButtonApply.text = getString(R.string.button_apply, selectedCount)
        }
    }

    private fun onAlbumSelected(album: Album) {
        if (album.isAll && album.isEmpty) {
            mContainer.visibility = View.GONE
            mEmptyView.visibility = View.VISIBLE
        } else {
            mContainer.visibility = View.VISIBLE
            mEmptyView.visibility = View.GONE
            val fragment = ZhihuImageListGridFragment.instance(album)
            fragment.injectDependencies(this, this, this)
            childFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, fragment, ZhihuImageListGridFragment::class.java.simpleName)
                    .commitAllowingStateLoss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAlbumCollection.onDestroy()
    }

    override fun onMediaClick(album: Album?, item: Item, adapterPosition: Int) {
        val intent = Intent(context, AlbumPreviewActivity::class.java)
        intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album)
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item)
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.dataWithBundle)
        startActivityForResult(intent, ZhihuImagePickerActivity.REQUEST_CODE_PREVIEW)
    }

    override fun onUpdate() {
        updateBottomToolbar()
    }

    override fun provideSelectedItemCollection(): SelectedItemCollection {
        return mSelectedCollection
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button_preview -> {
                val intent = Intent(context, SelectedPreviewActivity::class.java)
                intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.dataWithBundle)
                startActivityForResult(intent, ZhihuImagePickerActivity.REQUEST_CODE_PREVIEW)
            }
            R.id.button_apply -> emitSelectUri()
            R.id.button_back -> activity!!.onBackPressed()
        }
    }

    private fun emitSelectUri() {
        val selectedUris = mSelectedCollection.asListOfUri() as ArrayList<Uri>
        for (uri in selectedUris) {
            publishSubject.onNext(parseResultNoExtraData(uri))
        }
        endPickImage()
    }

    private fun endPickImage() {
        publishSubject.onComplete()
        closure()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK)
            return

        if (requestCode == ZhihuImagePickerActivity.REQUEST_CODE_PREVIEW) {
            val resultBundle = data!!.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE)
            val selected = resultBundle.getParcelableArrayList<Item>(SelectedItemCollection.STATE_SELECTION)
            val collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED)
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {  // apply event
                if (selected != null) {
                    for (item in selected) {
                        if (activity is ZhihuImagePickerActivity) {
                            ActivityPickerViewController.instance.emitResult(
                                    parseResultNoExtraData(item.contentUri!!)
                            )
                        } else {
                            publishSubject.onNext(
                                    parseResultNoExtraData(item.contentUri!!)
                            )
                        }
                    }
                }
                closure()
            } else {         // back event
                mSelectedCollection.overwrite(selected!!, collectionType)
                val weChatListFragment = childFragmentManager.findFragmentByTag(
                        ZhihuImageListGridFragment::class.java.simpleName)
                if (weChatListFragment is ZhihuImageListGridFragment) {
                    weChatListFragment.refreshMediaGrid()
                }
                updateBottomToolbar()
            }
        }
    }
}