package com.emall.net.fragment.staticPages

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

class MyBrowser(private val requireContext: Context) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        view.loadUrl(url!!)
        return true
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        //   parentDialogListener.hideProgressDialog();
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        super.onReceivedError(view, request, error)
        // parentDialogListener.showProgressDialog();
        Toast.makeText(requireContext, "Something went wrong", Toast.LENGTH_SHORT).show()
    }


    override fun onReceivedError(
        view: WebView?,
        errorCode: Int,
        description: String,
        failingUrl: String?,
    ) {
        //    parentDialogListener.showProgressDialog();
        Toast.makeText(requireContext, "Error$description$errorCode", Toast.LENGTH_SHORT).show()
    }
}

