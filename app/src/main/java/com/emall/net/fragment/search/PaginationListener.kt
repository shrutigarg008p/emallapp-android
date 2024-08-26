package com.emall.net.fragment.search

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(
    private val gridLayoutManager: GridLayoutManager,
) : RecyclerView.OnScrollListener() {
    private var previousTotal = 0
    private var loading = true
    private var visibleThreshold = 0
    var firstVisibleItem = 0
    var visibleItemCount = 0
    var totalItemCount = 0
    private val startingPageIndex = 0
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        visibleItemCount = recyclerView.childCount
        totalItemCount = gridLayoutManager.itemCount
        firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition()
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }
        if (!loading
            && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold
        ) {
            // End has been reached
            // Do something
            current_page++
            onLoadMore(current_page)
            loading = true
        }
    }

    abstract fun onLoadMore(current_page: Int)

    // Call whenever performing new searches
    fun resetState() {
        current_page = startingPageIndex
        previousTotal = 0
        loading = true
    }

    fun resetValues() {
        current_page = 1
        previousTotal = 0
        loading = true
        visibleThreshold = 1
        firstVisibleItem = 0
        visibleItemCount = 0
        totalItemCount = 0
    }

    companion object {
        var TAG = PaginationListener::class.java
            .simpleName
        var current_page = 1
    }
}