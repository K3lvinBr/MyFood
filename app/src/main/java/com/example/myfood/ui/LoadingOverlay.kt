package com.example.myfood.ui

import android.view.View
import android.widget.FrameLayout

class LoadingOverlay(loadingOverlay: FrameLayout) {
    private var loadingOverlay = loadingOverlay

    fun showLoading() {
        loadingOverlay.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loadingOverlay.visibility = View.GONE
    }
}