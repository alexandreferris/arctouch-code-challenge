package com.arctouch.codechallenge.util.scroll

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import org.apache.commons.lang3.math.NumberUtils

abstract class EndlessScrollListener: RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private var visibleThreshold: Int = NumberUtils.INTEGER_ZERO
    // The current offset index of data you have loaded
    private var currentPage = 1
    // The total number of items in the dataset after the last load
    private var previousTotalItemCount = 0
    // True if we are still waiting for the last set of data to load.
    private var loading = true
    // Sets the starting page index
    private val startingPageIndex = 1

    private var mLayoutManager: RecyclerView.LayoutManager

    constructor(layoutManager: LinearLayoutManager, visibleThreshold: Int) {
        this.mLayoutManager = layoutManager
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: GridLayoutManager, visibleThreshold: Int) {
        this.mLayoutManager = layoutManager
        visibleThreshold.times(layoutManager.spanCount)
        this.visibleThreshold = visibleThreshold
    }

    constructor(layoutManager: StaggeredGridLayoutManager, visibleThreshold: Int) {
        this.mLayoutManager = layoutManager
        visibleThreshold.times(layoutManager.spanCount)
        this.visibleThreshold = visibleThreshold
    }

    fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0
        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0
        val totalItemCount = mLayoutManager.itemCount

        if (mLayoutManager is StaggeredGridLayoutManager) {
            val lastVisibleItemPositions = (mLayoutManager as StaggeredGridLayoutManager).findLastCompletelyVisibleItemPositions(null)
            // get maximum element within the list
            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
        } else if (mLayoutManager is GridLayoutManager) {
            lastVisibleItemPosition = (mLayoutManager as GridLayoutManager).findLastCompletelyVisibleItemPosition()
        } else if (mLayoutManager is LinearLayoutManager) {
            lastVisibleItemPosition = (mLayoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex
            this.previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                this.loading = true
            }
        }
        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && totalItemCount > previousTotalItemCount) {
            loading = false
            previousTotalItemCount = totalItemCount
        }

        if (!loading && lastVisibleItemPosition >= (visibleThreshold * this.currentPage)) {
            currentPage++
            onLoadMore(currentPage, totalItemCount, view)
            loading = true
        }
    }

    // Call this method whenever performing new searches
    fun resetState() {
        this.currentPage = this.startingPageIndex
        this.previousTotalItemCount = 0
        this.loading = true
    }

    // Defines the process for actually loading more data based on page
    abstract fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?)
}