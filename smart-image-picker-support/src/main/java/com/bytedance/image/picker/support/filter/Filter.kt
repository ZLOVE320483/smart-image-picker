package com.bytedance.image.picker.support.filter

import android.content.Context
import com.bytedance.image.picker.support.MimeType
import com.bytedance.image.picker.support.entity.IncapableCause
import com.bytedance.image.picker.support.entity.Item

abstract class Filter {

    /**
     * Against what mime types this filter applies.
     */
    protected abstract fun constraintTypes(): Set<MimeType>

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, [IncapableCause] if not selectable.
     */
    abstract fun filter(context: Context, item: Item): IncapableCause

    /**
     * Whether an [Item] need filtering.
     */
    protected fun needFiltering(context: Context, item: Item): Boolean {
        for (type in constraintTypes()) {
            if (type.checkType(context.contentResolver, item.contentUri)) {
                return true
            }
        }
        return false
    }

    companion object {
        /**
         * Convenient constant for a minimum value.
         */
        const val MIN = 0
        /**
         * Convenient constant for a maximum value.
         */
        const val MAX = Integer.MAX_VALUE
        /**
         * Convenient constant for 1024.
         */
        const val K = 1024
    }
}