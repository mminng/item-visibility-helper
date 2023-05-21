package com.github.mminng.itemvisibility

import android.graphics.Rect
import android.util.Pair
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.mminng.itemvisibility.listener.ItemStateChangeListener
import com.github.mminng.itemvisibility.logger.loggerD
import com.github.mminng.itemvisibility.logger.loggerI

/**
 * Created by zh on 2023/4/27.
 *
 * 1.滑动中，如果激活中的Item被移出显示范围，就关闭。
 * 2.滑动停止，如果激活中的Item可见范围大于50%则保持不变，否则关闭。
 *   同时激活显示范围中可见范围最大并且大于50%的Item，如果可见范围最大的依然是激活中的Item，
 *   那么可见范围大于50%则保持不变，否则不会关闭，会进入暂停状态，等可见范围不小于50%时恢复。
 *   另外，如果激活中的Item可见范围小于50%，但是显示范围中没有其他Item需要激活，
 *   此时这个Item也进入会暂停状态，等可见范围不小于50%时恢复。
 */
class ItemVisibilityHelper : RecyclerView.OnChildAttachStateChangeListener {
    private var _recyclerView: RecyclerView? = null
    private var _layoutManager: LayoutManager? = null
    private var _orientation: Int = RecyclerView.VERTICAL
    private var _isReverseLayout: Boolean = false
    private var _targetViewId: Int = View.NO_ID
    private var _activatePosition: Int = RecyclerView.NO_POSITION
    private var _isTopCloser: Boolean = true
    private var _isPauseState: Boolean = false
    private var _isAutoActivate: Boolean = true
    private val outRect: Rect = Rect()
    private var _itemStateChangeListener: ItemStateChangeListener? = null
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        var scrolled = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && scrolled) {
                scrolled = false
                val newItem: Pair<View, Int> = findNewItem()
                if (newItem.first == null) return
                //如果找到的Item是已激活的Item
                if (isActivateItem(newItem.second)) {
                    val percent = getItemVisiblePercent(newItem.first)
                    loggerD("Found item was activated, visible percent is $percent%.")
                    if (percent < 50) {
                        pauseItem(newItem.first, newItem.second)
                    } else {
                        resumeItem(newItem.first, newItem.second)
                    }
                } else {
                    val oldItem = getItem(_activatePosition)
                    val oldPercent = getItemVisiblePercent(oldItem)
                    if (_isAutoActivate) {
                        if (oldPercent < 50) {
                            activateItem(newItem.first, newItem.second, _activatePosition)
                        } else {
                            loggerD("Current item visible percent >=50%, do nothing.")
                        }
                    } else {
                        loggerD("No other item need to be activated.")
                        if (oldPercent < 50) {
                            oldItem?.let { pauseItem(it, _activatePosition) }
                        } else {
                            oldItem?.let { resumeItem(it, _activatePosition) }
                        }
                    }
                }
            }
        }

        /**
         * 此处做闭关操作不准确
         * 因此在[onChildViewDetachedFromWindow]中会再次检查，如果有激活的立即关闭
         */
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx != 0 || dy != 0) scrolled = true
            //有已激活的Item，检查其可见范围
            if (hasActivateItem()) {
                val item = getItem(_activatePosition)
                if (item != null) {
                    val percent = getItemVisiblePercent(item)
                    loggerD("Current item visible percent is $percent%.")
                    if (percent == 0) {
                        deactivateItem(item, _activatePosition)
                    }
                }
            }
        }
    }

    override fun onChildViewAttachedToWindow(view: View) {
        //NO OP
    }

    override fun onChildViewDetachedFromWindow(view: View) {
        if (hasActivateItem()) {
            val detachedPosition: Int =
                _recyclerView?.findContainingViewHolder(view)?.adapterPosition
                    ?: RecyclerView.NO_POSITION
            if (isActivateItem(detachedPosition)) {
                deactivateItem(view, detachedPosition)
            }
        }
    }

    /**
     * 将[ItemVisibilityHelper]附加到[recyclerView]。
     * 如果未指定[targetViewId]，[ItemVisibilityHelper]会以整个Item的高度进行可见范围的计算。
     */
    fun attachToRecyclerView(
        recyclerView: RecyclerView,
        @IdRes targetViewId: Int = View.NO_ID,
        autoActivate: Boolean = true,
        listener: ItemStateChangeListener.() -> Unit
    ) {
        if (_recyclerView === recyclerView) return
        removeListener()
        _recyclerView = recyclerView
        when (recyclerView.layoutManager) {
            is LinearLayoutManager -> {
                _layoutManager = recyclerView.layoutManager as LinearLayoutManager
                _orientation = (_layoutManager as LinearLayoutManager).orientation
                _isReverseLayout = (_layoutManager as LinearLayoutManager).reverseLayout
            }

            is StaggeredGridLayoutManager -> {
                _layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                _orientation = (_layoutManager as StaggeredGridLayoutManager).orientation
                _isReverseLayout = (_layoutManager as StaggeredGridLayoutManager).reverseLayout
            }

            else -> {
                throw RuntimeException(
                    "Only support RecyclerView with LinearLayoutManager, " +
                            "GridLayoutManager or StaggeredGridLayoutManager."
                )
            }
        }
        _targetViewId = targetViewId
        _isAutoActivate = autoActivate
        setupListener(listener)
        loggerI(
            "ItemVisibilityHelper was attached to RecyclerView.\n" +
                    "Has targetViewId: ${targetViewId != View.NO_ID}.\n" +
                    "AutoActivate: $autoActivate."
        )
    }

    /**
     * 激活显示范围中可见百分比最大的Item
     */
    fun activateItem() {
        if (hasActivateItem()) {
            loggerD("Current has an activated item.")
            return
        }
        val newItem: Pair<View, Int> = findNewItem()
        activateItem(newItem.first, newItem.second, _activatePosition)
    }

    /**
     * 激活指定[position]的Item
     */
    fun activateItem(position: Int) {
        if (isActivateItem(position)) {
            loggerD("Current item was activated.")
            return
        }
        activateItem(getItem(position), position, _activatePosition)
    }

    /**
     * 关闭已激活的Item
     */
    fun deactivateItem() {
        if (!hasActivateItem()) {
            loggerD("Has not any activated item.")
            return
        }
        getItem(_activatePosition)?.let { deactivateItem(it, _activatePosition) }
    }

    private fun activateItem(view: View?, newPosition: Int, oldPosition: Int) {
        if (getItemVisiblePercent(view) < 50 || view == null) {
            loggerD("Prepared item visible percent <50%, shouldn't activate it.")
            return
        }
        getItem(oldPosition)?.let { deactivateItem(it, oldPosition) }
        _activatePosition = newPosition
        _itemStateChangeListener?.onActivateItem(view, newPosition)
        loggerI("Activated item with position $newPosition.")
    }

    private fun deactivateItem(view: View, position: Int) {
        _activatePosition = RecyclerView.NO_POSITION
        _isPauseState = false
        _itemStateChangeListener?.onDeactivateItem(view, position)
        loggerI("Deactivated item with position $position.")
    }

    private fun pauseItem(view: View, position: Int) {
        if (!_isPauseState) {
            _isPauseState = true
            _itemStateChangeListener?.onPauseItem(view, position)
            loggerI("Paused item with position $position.")
        }
    }

    private fun resumeItem(view: View, position: Int) {
        if (_isPauseState) {
            _isPauseState = false
            _itemStateChangeListener?.onResumeItem(view, position)
            loggerI("Resumed item with position $position.")
        }
    }

    /**
     * 向下寻找可见范围最大的Item，从[firstPosition]到[lastPosition]。
     * 如果可见范围等于100则立即返回此Item。否则做比较，返回可见范围最大的Item及其Position。
     * 如果可见范围等于0，则认为没有找到。
     */
    private fun findNextMostVisibleItem(firstPosition: Int, lastPosition: Int): Pair<View, Int> {
        loggerD("Find next: from $firstPosition to $lastPosition.")
        var max = 0
        var position = RecyclerView.NO_POSITION
        for (index in firstPosition..lastPosition) {
            val item: View? = getItem(index)
            if (item != null) {
                val percent: Int = getItemVisiblePercent(item)
                if (percent == 100) {
                    loggerD("Find next: position=$index max=100 return.")
                    return Pair.create(item, index)
                }
                if (max < percent) {
                    max = percent
                    position = index
                }
                loggerD("Find next: position=$index max=$percent.")
            }
        }
        if (max > 0) {
            getItem(position)?.let {
                loggerD("Find next: return position $position.")
                return Pair.create(it, position)
            }
        }
        loggerD("Find next: not find.")
        return Pair.create(null, position)
    }

    /**
     * 向上寻找可见范围最大的Item，从[lastPosition]到[firstPosition]。
     * 如果可见范围等于100则立即返回此Item。否则做比较，返回可见范围最大的Item及其Position。
     * 如果可见范围等于0，则认为没有找到。
     */
    private fun findLastMostVisibleItem(lastPosition: Int, firstPosition: Int): Pair<View, Int> {
        loggerD("Find last: from $lastPosition to $firstPosition.")
        var max = 0
        var position = RecyclerView.NO_POSITION
        for (index in lastPosition downTo firstPosition) {
            val item: View? = getItem(index)
            if (item != null) {
                val percent: Int = getItemVisiblePercent(item)
                if (percent == 100) {
                    loggerD("Find last: position=$index max=100 return.")
                    return Pair.create(item, index)
                }
                if (max < percent) {
                    max = percent
                    position = index
                }
                loggerD("Find last: position=$index max=$percent.")
            }
        }
        if (max > 0) {
            getItem(position)?.let {
                loggerD("Find last: return position $position.")
                return Pair.create(it, position)
            }
        }
        loggerD("Find last: not find.")
        return Pair.create(null, position)
    }

    /**
     * 依据[orientation]计算[view]可见百分比。
     */
    private fun calculateVisiblePercent(view: View?, orientation: Int): Int {
        if (view == null) return 0
        if (view.getLocalVisibleRect(outRect)) {
            val viewHeight: Int = if (orientation == RecyclerView.HORIZONTAL)
                view.width else view.height
            val displayHeight: Int = if (orientation == RecyclerView.HORIZONTAL) {
                _recyclerView?.computeHorizontalScrollExtent() ?: 0
            } else {
                _recyclerView?.computeVerticalScrollExtent() ?: 0
            }
            //outRect.top==outRect.left
            val top: Int = if (orientation == RecyclerView.HORIZONTAL)
                outRect.left else outRect.top
            //outRect.bottom==outRect.right
            val bottom: Int = if (orientation == RecyclerView.HORIZONTAL)
                outRect.right else outRect.bottom
            /*哪一边被遮挡的多则认为更靠近那一边。例如，顶部被遮挡的大于底部被遮挡的，则认为view的位置更靠近顶部。
              有可能顶部和底部被遮挡的一样则默认view更靠近顶部。
              实际上也无关紧要，因为出现相等的情况发生在，
              1、view高度大于显示高度且view正好在显示范围的正中，顶部和底部的遮挡范围相同，
              2、view高度小于等于显示范围高度，此时顶部和底部都没有被遮挡，顶部和底部的遮挡范围都为0，
              此时不会发生激活Item动作，不影响我们去判断是更靠近顶部还是底部。
              而随着view的滑动总有一边会被遮挡或遮挡范围不一致，进而能准确判断出是更靠近顶部还是底部。*/
            //top等于顶部被遮挡的高度，(viewHeight - bottom)等于底部被遮挡的高度。
            _isTopCloser = if (_isReverseLayout)
                top < (viewHeight - bottom) else top >= (viewHeight - bottom)
            //view高度大于等于显示范围高度的情况。
            if (viewHeight >= displayHeight) {
                //(bottom - top)等于view在显示范围中的高度。
                return (bottom - top) * 100 / displayHeight
            }
            /*(top > 0)顶部被遮挡。
              (viewHeight - bottom > 0)底部被遮挡。
              以上条件有可能都不成立，此时view的高度小于或等于显示范围高度，并且完全在显示范围中，
              因此percent默认为100。*/
            var percent = 100
            if (top > 0) {
                percent = (viewHeight - top) * 100 / viewHeight
            } else if (viewHeight - bottom > 0) {
                percent = bottom * 100 / viewHeight
            }
            return percent
        }
        return 0
    }

    private fun findNewItem(): Pair<View, Int> {
        val firstPosition: Int
        val lastPosition: Int
        if (_layoutManager is StaggeredGridLayoutManager) {
            firstPosition =
                (_layoutManager as StaggeredGridLayoutManager).findFirstVisibleItemPositions(
                    null
                ).minOrNull() ?: RecyclerView.NO_POSITION
            lastPosition =
                (_layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(
                    null
                ).maxOrNull() ?: RecyclerView.NO_POSITION
        } else {
            firstPosition = (_layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            lastPosition = (_layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        return if (_isTopCloser)
            findNextMostVisibleItem(firstPosition, lastPosition) else
            findLastMostVisibleItem(lastPosition, firstPosition)
    }

    private fun getItem(position: Int): View? = _layoutManager?.findViewByPosition(position)

    private fun getItemVisiblePercent(item: View?): Int {
        if (_targetViewId != View.NO_ID) {
            return calculateVisiblePercent(item?.findViewById(_targetViewId), _orientation)
        }
        return calculateVisiblePercent(item, _orientation)
    }

    private fun hasActivateItem(): Boolean = _activatePosition != RecyclerView.NO_POSITION

    private fun isActivateItem(position: Int): Boolean = _activatePosition == position

    private fun setupListener(listener: ItemStateChangeListener.() -> Unit) {
        _recyclerView?.addOnScrollListener(scrollListener)
        _recyclerView?.addOnChildAttachStateChangeListener(this)
        val itemStateChangeListener = ItemStateChangeListener()
        itemStateChangeListener.listener()
        _itemStateChangeListener = itemStateChangeListener
    }

    private fun removeListener() {
        _recyclerView?.removeOnScrollListener(scrollListener)
        _recyclerView?.removeOnChildAttachStateChangeListener(this)
        _itemStateChangeListener = null
    }
}