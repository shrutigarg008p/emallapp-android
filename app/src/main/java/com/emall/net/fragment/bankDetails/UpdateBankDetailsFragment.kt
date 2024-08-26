package com.emall.net.fragment.bankDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.bankList.BankList
import com.emall.net.network.model.updateBankDetails.BankRequestParams
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.popBackStack
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_update_bank_details.*

class UpdateBankDetailsFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var banks = ArrayList<BankList>()
    private var bankName: String? = ""
    private var bankList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_update_bank_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        update_btn.setOnClickListener(this)
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient()
                .create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        viewModel.getBankList(PreferenceHelper.readString("email")!!,
            PreferenceHelper.readString("password")!!, "Bearer $token",Utility.getLanguage()).observe(
            viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA.let { it ->
                                bankList.clear()
                                banks.clear()
                                bankList.add("Select Bank")
                                for (bank in resource.data?.DATA!!) {
                                    bankList.add(bank.value)
                                }
                                bank_spinner.setItems(bankList)
                                banks = it!!
                            }
                        }
                        Status.ERROR -> {
                            Log.d("TAG", "onViewCreated: " + it.message)
                        }
                        Status.LOADING -> {
                            Log.d("TAG", "onViewCreated: ")
                        }
                    }
                }
            }
        )

        bank_spinner.setOnItemSelectedListener { view, position, id, item ->
            when {
                position > 0 -> bankName = banks[position - 1].value
            }
        }
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.update_btn -> {
                viewModel.updateBankDetails(
                    BankRequestParams(bankName!!,
                        mobile_edit_text.text.toString(),
                        iban_edit_text.text.toString(),
                        stc_pay_wallet_number.text.toString()), "Bearer $token",Utility.getLanguage())
                    .observe(viewLifecycleOwner, {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.let { it ->
                                        progress_bar.makeGone()
                                        constraintLayout.snack(it.MESSAGE)
                                        (activity as SellerActivity).popBackStack()
                                    }
                                }
                                Status.ERROR -> {
                                    progress_bar.makeGone()
                                    constraintLayout.snack(it.message!!)
                                }
                                Status.LOADING -> progress_bar.makeVisible()
                            }
                        }
                    })
            }
        }
    }
}