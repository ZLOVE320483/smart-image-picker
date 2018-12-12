package com.bytedance.image.picker.zhihu.ui

import android.database.Cursor
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bytedance.image.picker.support.entity.Album
import com.bytedance.image.picker.support.entity.Item
import com.bytedance.image.picker.support.entity.SelectionSpec
import com.bytedance.image.picker.support.model.AlbumMediaCollection
import com.bytedance.image.picker.support.model.SelectedItemCollection
import com.bytedance.image.picker.support.ui.adapter.AlbumMediaAdapter
import com.bytedance.image.picker.support.ui.widget.MediaGridInset
import com.bytedance.image.picker.support.utils.UIUtils
import com.bytedance.image.picker.zhihu.R

class ZhihuImageListGridFragment : androidx.fragment.app.Fragment(), AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener, AlbumMediaCollection.AlbumMediaCallbacks {

    private val mAlbumMediaCollection = AlbumMediaCollection()
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: AlbumMediaAdapter
    private var mSelectionProvider: SelectionProvider? = null
    private var mCheckStateListener: AlbumMediaAdapter.CheckStateListener? = null
    private var mOnMediaClickListener: AlbumMediaAdapter.OnMediaClickListener? = null

    fun injectDependencies(selectionProvider: SelectionProvider?,
                           checkStateListener: AlbumMediaAdapter.CheckStateListener,
                           mediaClickListener: AlbumMediaAdapter.OnMediaClickListener) {
        if (null != selectionProvider) {
            this.mSelectionProvider = selectionProvider
        } else {
            throw IllegalStateException("Context must implement SelectionProvider.")
        }
        this.mCheckStateListener = checkStateListener
        this.mOnMediaClickListener = mediaClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val contextThemeWrapper = ContextThemeWrapper(activity, SelectionSpec.instance.themeId)
        val localInflater = inflater
                .cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.fragment_media_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.recyclerview)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val album = arguments!!.getParcelable<Album>(EXTRA_ALBUM)

        mAdapter = AlbumMediaAdapter(
                context!!,
                mSelectionProvider!!.provideSelectedItemCollection(),
                mRecyclerView
        ).apply {
            registerCheckStateListener(this@ZhihuImageListGridFragment)
            registerOnMediaClickListener(this@ZhihuImageListGridFragment)
        }
        mRecyclerView.setHasFixedSize(true)

        val spanCount: Int
        val selectionSpec = SelectionSpec.instance
        spanCount = if (selectionSpec.gridExpectedSize > 0) {
            UIUtils.spanCount(context!!, selectionSpec.gridExpectedSize)
        } else {
            selectionSpec.spanCount
        }
        mRecyclerView.layoutManager = GridLayoutManager(context, spanCount)

        mRecyclerView.addItemDecoration(
                MediaGridInset(spanCount,
                        resources.getDimensionPixelSize(R.dimen.media_grid_spacing),
                        false
                )
        )
        mRecyclerView.adapter = mAdapter

        mAlbumMediaCollection.onCreate(activity!!, this)
        mAlbumMediaCollection.load(album, selectionSpec.capture)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mAlbumMediaCollection.onDestroy()
    }

    fun refreshMediaGrid() {
        mAdapter.notifyDataSetChanged()
    }

    fun refreshSelection() {
        mAdapter.refreshSelection()
    }

    override fun onUpdate() {
        // notify outer Activity that check state changed
        mCheckStateListener?.onUpdate()
    }

    override fun onMediaClick(album: Album?, item: Item, adapterPosition: Int) {
        mOnMediaClickListener?.onMediaClick(arguments!!.getParcelable(EXTRA_ALBUM),
                item, adapterPosition)
    }

    override fun onAlbumMediaLoad(cursor: Cursor) {
        mAdapter.swapCursor(cursor)
    }

    override fun onAlbumMediaReset() {
        mAdapter.swapCursor(null)
    }

    interface SelectionProvider {
        fun provideSelectedItemCollection(): SelectedItemCollection
    }

    companion object {

        const val EXTRA_ALBUM = "extra_album"

        fun instance(album: Album): ZhihuImageListGridFragment {
            val fragment = ZhihuImageListGridFragment()
            val args = Bundle()
            args.putParcelable(EXTRA_ALBUM, album)
            fragment.arguments = args
            return fragment
        }
    }
}