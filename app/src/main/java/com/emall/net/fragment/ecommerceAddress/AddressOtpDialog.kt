package com.emall.net.fragment.ecommerceAddress

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.Constants
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Status
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_address_otp_dialog.*

class AddressOtpDialog : DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private var mobileNumber: String = ""
    private var handler: Handler? = null
    private var handlerTask: Runnable? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_address_otp_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            mobileNumber = arguments?.getString("mobile_number")!!
        }
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUp()
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window!!.setLayout(width, height)
        }
    }

    private fun setUp() {
        verify_code_button.setOnClickListener {
            if (ViewUtils.checkTextViewValidation(
                    otp_value,
                    getString(R.string.enter_otp)
                )
            ) {
                verifyOtp()
            }
        }
        resend_code_button.setOnClickListener {
            resendOtp()
        }
        initTimer()
    }

    private fun verifyOtp() {
        viewModel.verifyOtp(mobileNumber,
            otp_value.text.toString())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    if (it.success) {
                                        handler!!.removeCallbacks(handlerTask!!)
                                        val intent = Intent()
                                        targetFragment!!.onActivityResult(1002,
                                            Activity.RESULT_OK,
                                            intent)
                                        dialog!!.cancel()
                                    } else {
                                        Toast.makeText(requireContext(), it.msg, Toast.LENGTH_LONG)
                                            .show()
                                    }
                                }
                            }
                            Status.ERROR -> {
                                it.message?.let { it1 -> Log.e("error", it1) }
                                activity?.let { ViewUtils.hideProgressBar() }
                            }
                            Status.LOADING -> {
                                activity?.let { ViewUtils.showProgressBar(it) }
                            }
                        }
                    }
                })
    }

    private fun resendOtp() {
        viewModel.sendOtp(mobileNumber,
            1,
            PreferenceHelper.readInt(
                Constants.CUSTOMER_ID)!!, 0)
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    if (it.success) {
                                        Toast.makeText(requireContext(),
                                            R.string.code_sent_successfully,
                                            Toast.LENGTH_SHORT).show()
                                        initTimer()
                                    } else {
                                        Toast.makeText(requireContext(),
                                            R.string.send_failed_retry,
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            Status.ERROR -> {
                                it.message?.let { it1 -> Log.e("error", it1) }
                                activity?.let { ViewUtils.hideProgressBar() }
                            }
                            Status.LOADING -> {
                                activity?.let { ViewUtils.showProgressBar(it) }
                            }
                        }
                    }
                })
    }

    private fun initTimer() {
        var i = 30
        resend_code_button.isEnabled = false
        resend_code_button.setBackgroundResource(R.drawable.ic_gray_gradient)
        resend_text.makeVisible()
        handler = Handler()
        handlerTask = Runnable {
            // set Resend text message
            val newString: String =
                requireContext().getString(R.string.resend_code_after_text).replace(
                    "30",
                    "$i")
            resend_text.text = newString
            i--
            handlerTask?.let { handler!!.postDelayed(it, 1000) }
            if (i == 0) {
                handler!!.removeCallbacks(handlerTask!!)
                resend_code_button.isEnabled = true
                resend_text.makeGone()
                resend_code_button.setBackgroundResource(R.drawable.ic_charcoal_black_gradient_btn_color)
            }
        }
        handlerTask!!.run()
    }

}