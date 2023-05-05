package com.github.mminng.itemvisibility.listener

import android.view.View

/**
 * Created by zh on 2023/4/27.
 */
internal interface OnItemStateChangeListener {

    /**
     * 激活Item，提供被激活的Item[view]和Item[position]。
     */
    fun onActivateItem(view: View, position: Int)

    /**
     * 关闭Item，提供被关闭的Item[view]和Item[position]。
     */
    fun onDeactivateItem(view: View, position: Int)

    /**
     * 暂停Item，提供被暂停的Item[view]和Item[position]。
     */
    fun onPauseItem(view: View, position: Int)

    /**
     * 恢复Item，提供被恢复的Item[view]和Item[position]。
     */
    fun onResumeItem(view: View, position: Int)
}