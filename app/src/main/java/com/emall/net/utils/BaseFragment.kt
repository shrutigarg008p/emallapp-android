package com.emall.net.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.emall.net.network.api.*
import com.emall.net.viewmodel.*
import kotlinx.coroutines.*

open class BaseFragment : Fragment() {

    internal val name = BaseFragment::class.java.simpleName

    private val job = Job()
    internal val scopeMainThread = CoroutineScope(job + Dispatchers.Main)
    internal val scopeIO = CoroutineScope(job + Dispatchers.IO)

    private val progressDialog = CustomProgressDialog()
    private val progressBar = ProgressBar()

    open suspend fun getReCommerceToken(): String =
        (activity as BaseActivity).getSellerEvaluatorToken()

    open suspend fun getECommerceToken() : String = (activity as BaseActivity).getECommerceToken()

    open suspend fun getReCommerceViewModel(): MainViewModel =
        ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

    open suspend fun getECommerceViewModel(): MainViewModel =
        ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    open fun showProgressDialog() {
        progressDialog.show(requireContext())
    }

    open fun hideProgressDialog() {
        progressDialog.dialog.dismiss()
    }

    open fun showProgressBar() {
        progressBar.show(requireContext())
    }

    open fun hideProgressBar() {
        progressBar.dialog.dismiss()
    }

}