package com.github.mminng.itemvisibility.listener

import android.view.View

/**
 * Created by zh on 2023/4/27.
 */
class ItemStateChangeListener : OnItemStateChangeListener {

    private var _activateItem: ((view: View, position: Int) -> Unit)? = null
    private var _deactivateItem: ((view: View, position: Int) -> Unit)? = null
    private var _pauseItem: ((view: View, position: Int) -> Unit)? = null
    private var _resumeItem: ((view: View, position: Int) -> Unit)? = null

    override fun onActivateItem(view: View, position: Int) {
        _activateItem?.invoke(view, position)
    }

    override fun onDeactivateItem(view: View, position: Int) {
        _deactivateItem?.invoke(view, position)
    }

    override fun onPauseItem(view: View, position: Int) {
        _pauseItem?.invoke(view, position)
    }

    override fun onResumeItem(view: View, position: Int) {
        _resumeItem?.invoke(view, position)
    }

    fun activateItem(listener: (view: View, position: Int) -> Unit) {
        _activateItem = listener
    }

    fun deactivateItem(listener: (view: View, position: Int) -> Unit) {
        _deactivateItem = listener
    }

    fun pauseItem(listener: (view: View, position: Int) -> Unit) {
        _pauseItem = listener
    }

    fun resumeItem(listener: (view: View, position: Int) -> Unit) {
        _resumeItem = listener
    }
}