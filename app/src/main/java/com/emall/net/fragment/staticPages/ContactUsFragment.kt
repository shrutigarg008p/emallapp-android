package com.emall.net.fragment.staticPages

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.contactUsRequest.ContactUsRequest
import com.emall.net.network.model.contactUsRequest.ContactUsRequestParam
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_contact_us.*

class ContactUsFragment : Fragment() {
    private val requestCall = 1
    private lateinit var viewModel: MainViewModel
    private var isAllConditionFulfilled = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("staticPages")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.contact_us))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("staticPages")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.contact_us))
            }
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

    private fun setUp() {
        whatsapp_button.setOnClickListener(View.OnClickListener {
            val text = getText(R.string.hello_emall_team)// Replace with your message.
            val toNumber =
                "+966558645199"// Replace with mobile phone number without +Sign or leading zeros, but with country code
            val pm = requireActivity().packageManager
            try {
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("http://api.whatsapp.com/send?phone=$toNumber&text=$text")
                startActivity(intent)
            } catch (e: PackageManager.NameNotFoundException) {
                Toast.makeText(activity,
                    R.string.whatsapp_not_installed,
                    Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        })

        call_button.setOnClickListener(View.OnClickListener {
            Log.e("call button", "clicked")
            makePhoneCall()
        })

        send_button.setOnClickListener(View.OnClickListener {
            (ViewUtils.checkTextViewValidation(
                name_value,
                getString(R.string.enter_name)
            ) && ViewUtils.firstLetterisNumber(
                name_value,
                getString(R.string.name_must_start_with_alphabet)
            ) && ViewUtils.checkTextViewValidation(
                email_value,
                getString(R.string.enter_email)
            ) && ViewUtils.isEmailValid(
                email_value,
                getString(R.string.enter_email)
            ) && ViewUtils.checkTextViewValidation(
                mobile_number_value,
                getString(R.string.enter_mobile)
            )).also { isAllConditionFulfilled = it }
            if (isAllConditionFulfilled) {
                contactUs()
            }
        })
    }

    private fun contactUs() {
        val contactUsRequest = ContactUsRequest(
            param = ContactUsRequestParam(
                email_value.text.toString(),
                name_value.text.toString(),
                message_value.text.toString(),
                mobile_number_value.text.toString()
            )
        )
        viewModel.contactUs(contactUsRequest, Utility.getLanguage())
            .observe(
                viewLifecycleOwner,
                { it ->
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                activity?.let { ViewUtils.hideProgressBar() }
                                resource.data?.let {
                                    Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT)
                                        .show()
                                    email_value.text.clear()
                                    name_value.text.clear()
                                    message_value.text.clear()
                                    mobile_number_value.text.clear()
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

    private fun makePhoneCall() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CALL_PHONE),
                requestCall
            )
        } else {
            val dial = "tel:+966558645199"
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray,
    ) {
        if (requestCode == requestCall) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall()
            } else {
                Toast.makeText(requireContext(), "Permission DENIED", Toast.LENGTH_SHORT).show()
            }
        }
    }
}