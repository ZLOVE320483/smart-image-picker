package com.bytedance.image.picker.support.ui.adapter

import android.view.ViewGroup
import com.bytedance.image.picker.support.entity.Item
import com.bytedance.image.picker.support.ui.PreviewItemFragment
import java.util.*

class PreviewPagerAdapter internal constructor(manager: androidx.fragment.app.FragmentManager,
                                               private val mListener: OnPrimaryItemSetListener?)
    : androidx.fragment.app.FragmentPagerAdapter(manager) {

    private val mItems = ArrayList<Item>()

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        return PreviewItemFragment.newInstance(mItems[position])
    }

    override fun getCount(): Int {
        return mItems.size
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        super.setPrimaryItem(container, position, `object`)
        mListener?.onPrimaryItemSet(position)
    }

    fun getMediaItem(position: Int): Item {
        return mItems[position]
    }

    fun addAll(items: List<Item>) {
        mItems.addAll(items)
    }

    internal interface OnPrimaryItemSetListener {

        fun onPrimaryItemSet(position: Int)
    }

}
