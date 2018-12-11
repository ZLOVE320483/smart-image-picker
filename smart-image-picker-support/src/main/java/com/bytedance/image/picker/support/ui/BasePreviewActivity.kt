package com.bytedance.image.picker.support.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.bytedance.image.picker.support.R
import com.bytedance.image.picker.support.entity.IncapableCause
import com.bytedance.image.picker.support.entity.Item
import com.bytedance.image.picker.support.entity.SelectionSpec
import com.bytedance.image.picker.support.model.SelectedItemCollection
import com.bytedance.image.picker.support.ui.adapter.PreviewPagerAdapter
import com.bytedance.image.picker.support.ui.widget.CheckView
import com.bytedance.image.picker.support.utils.PhotoMetadataUtils
import com.bytedance.image.picker.support.utils.Platform

abstract class BasePreviewActivity : AppCompatActivity(), androidx.viewpager.widget.ViewPager.OnPageChangeListener {

    protected val mSelectedCollection = SelectedItemCollection(this)
    protected lateinit var mSpec: SelectionSpec
    protected lateinit var mPager: androidx.viewpager.widget.ViewPager

    protected lateinit var mAdapter: PreviewPagerAdapter

    protected lateinit var mCheckView: CheckView
    protected lateinit var mButtonBack: TextView
    protected lateinit var mButtonApply: TextView
    protected lateinit var mSize: TextView

    protected var mPreviousPos = -1

    open protected val layoutRes: Int
        @LayoutRes
        get() = R.layout.activity_media_preview

    override fun onCreate(savedInstanceState: Bundle?) {
        mSpec = SelectionSpec.instance
        setTheme(mSpec.themeId)
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
        if (Platform.hasKitKat()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        if (mSpec.needOrientationRestriction()) {
            requestedOrientation = mSpec.orientation
        }

        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(intent.getBundleExtra(EXTRA_DEFAULT_BUNDLE))
        } else {
            mSelectedCollection.onCreate(savedInstanceState)
        }

        mButtonBack = findViewById(R.id.button_back)
        mButtonApply = findViewById(R.id.button_apply)
        mSize = findViewById(R.id.size)

        mButtonBack.setOnClickListener {
            onBackPressed()
        }
        mButtonApply.setOnClickListener {
            sendBackResult(true)
            finish()
        }

        mPager = findViewById(R.id.pager)
        mPager.addOnPageChangeListener(this)
        mAdapter = PreviewPagerAdapter(supportFragmentManager, null)
        mPager.adapter = mAdapter
        mCheckView = findViewById(R.id.check_view)
        mCheckView.setCountable(mSpec.countable)

        mCheckView.setOnClickListener {
            val item = mAdapter.getMediaItem(mPager.currentItem)
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item)
                if (mSpec.countable) {
                    mCheckView.setCheckedNum(CheckView.UNCHECKED)
                } else {
                    mCheckView.setChecked(false)
                }
            } else {
                if (assertAddSelection(item)) {
                    mSelectedCollection.add(item)
                    if (mSpec.countable) {
                        mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item))
                    } else {
                        mCheckView.setChecked(true)
                    }
                }
            }
            updateApplyButton()
        }
        updateApplyButton()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mSelectedCollection.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        sendBackResult(false)
        super.onBackPressed()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        val adapter = mPager.adapter as PreviewPagerAdapter
        if (mPreviousPos != -1 && mPreviousPos != position) {
            (adapter.instantiateItem(mPager, mPreviousPos) as PreviewItemFragment).resetView()

            val item = adapter.getMediaItem(position)
            if (mSpec.countable) {
                val checkedNum = mSelectedCollection.checkedNumOf(item)
                mCheckView.setCheckedNum(checkedNum)
                if (checkedNum > 0) {
                    mCheckView.isEnabled = true
                } else {
                    mCheckView.isEnabled = !mSelectedCollection.maxSelectableReached()
                }
            } else {
                val checked = mSelectedCollection.isSelected(item)
                mCheckView.setChecked(checked)
                if (checked) {
                    mCheckView.isEnabled = true
                } else {
                    mCheckView.isEnabled = !mSelectedCollection.maxSelectableReached()
                }
            }
            updateSize(item)
        }
        mPreviousPos = position
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun updateApplyButton() {
        val selectedCount = mSelectedCollection.count()
        if (selectedCount == 0) {
            mButtonApply.setText(R.string.button_apply_default)
            mButtonApply.isEnabled = false
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonApply.setText(R.string.button_apply_default)
            mButtonApply.isEnabled = true
        } else {
            mButtonApply.isEnabled = true
            mButtonApply.text = getString(R.string.button_apply, selectedCount)
        }
    }

    @SuppressLint("SetTextI18n")
    protected fun updateSize(item: Item) {
        if (item.isGif) {
            mSize.visibility = View.VISIBLE
            mSize.text = PhotoMetadataUtils.getSizeInMB(item.size).toString() + "M"
        } else {
            mSize.visibility = View.GONE
        }
    }

    private fun sendBackResult(apply: Boolean) {
        val intent = Intent()
        intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.dataWithBundle)
        intent.putExtra(EXTRA_RESULT_APPLY, apply)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun assertAddSelection(item: Item): Boolean {
        val cause = mSelectedCollection.isAcceptable(item)
        IncapableCause.handleCause(this, cause)
        return cause == null
    }

    companion object {

        const val EXTRA_DEFAULT_BUNDLE = "extra_default_bundle"
        const val EXTRA_RESULT_BUNDLE = "extra_result_bundle"
        const val EXTRA_RESULT_APPLY = "extra_result_apply"
    }
}
