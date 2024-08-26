package com.emall.net.fragment.evaluationDevicesWon

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.EvaluationDevicesWonAdapter
import com.emall.net.fragment.addressList.EvaluatorAddressListFragment
import com.emall.net.fragment.payment.PaymentSlipFragment
import com.emall.net.fragment.resolutionCenter.IssueDetailFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.RaiseAnIssueQueryParams
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.evaluationDevicesWon.EvaluationDevicesWon
import com.emall.net.utils.*
import com.emall.net.utils.Constants.SHIPMENT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.android.synthetic.main.fragment_product.heading
import kotlinx.android.synthetic.main.fragment_product.progress_bar
import kotlinx.android.synthetic.main.fragment_product.scrollView
import kotlinx.android.synthetic.main.stc_tap_fragment.*

class EvaluationDevicesWonFragment : Fragment(), OnItemClick {

    private lateinit var evaluationDeviceWonAdapter: EvaluationDevicesWonAdapter
    private val evaluationDevicesWonList = ArrayList<EvaluationDevicesWon>()
    private lateinit var viewModel: MainViewModel
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        sell_a_new_device_btn.makeGone()
        heading.makeGone()

        auction_recycler_view.setHasFixedSize(true)
        auction_recycler_view.layoutManager = LinearLayoutManager(activity)
        evaluationDeviceWonAdapter = EvaluationDevicesWonAdapter(evaluationDevicesWonList, this)
        auction_recycler_view.adapter = evaluationDeviceWonAdapter

        viewModel.getEvaluationWonDevices("Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.data.let {
                            progress_bar.makeGone()
                            evaluationDevicesWonList.clear()
                            evaluationDevicesWonList.addAll(it!!)
                            evaluationDeviceWonAdapter.notifyDataSetChanged()
                        }
                    }
                    Status.LOADING -> progress_bar.makeVisible()
                    Status.ERROR -> progress_bar.makeGone()
                }
            }
        })
    }

    override fun onItemClick(position: Int, type: String) {
        when {
            type.equals("address", true) -> {
                // navigate to saved address fragment
                val fragment = EvaluatorAddressListFragment()
                PreferenceHelper.writeBoolean("devicesWon", true)
                val bundle = Bundle()
                bundle.putInt("evaluationWonId", evaluationDevicesWonList[position].id)
                bundle.putInt("shipmentId",evaluationDevicesWonList[position].shipping_id)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
            }
            type.equals("re_evaluate", true) -> {
                // show re-evaluation
                showReEvaluationDialog(evaluationDevicesWonList[position].id)
            }
            type.equals("return device", true) -> {
                // return device
                returnDevice(evaluationDevicesWonList[position].id)
            }
            type.equals("raiseIssue", true) -> {
                // raise issue
                raiseIssueDialog(evaluationDevicesWonList[position].id)
            }type.equals("view issue",true) ->{
                // view issue
            val fragment = IssueDetailFragment()
            val bundle = Bundle()
            bundle.putInt("id",evaluationDevicesWonList[position].raise_issue.issue.id)
            fragment.arguments = bundle
            (activity as BuyerActivity).replaceFragment(fragment,R.id.container)
            }
            else -> {
                // proceed to offline payment
                val fragment = EvaluationOrderCompleteFragment()
                val bundle = Bundle()
                bundle.putInt("evaluationWonId", evaluationDevicesWonList[position].id)
                bundle.putInt(SHIPMENT_ID, evaluationDevicesWonList[position].shipping_id)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
            }
        }
    }

    private fun raiseIssueDialog(itemId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.resolution_center_dialog,
                scrollView.findViewById(R.id.content),
                false)
        builder.setView(view)

        val issueType = view.findViewById(R.id.item_edit_text) as AppCompatEditText
        val issueTitle = view.findViewById(R.id.title_edit_text) as AppCompatEditText
        val issueReason = view.findViewById(R.id.issue_description_edit_text) as AppCompatEditText
        val closeBtn = view.findViewById(R.id.close_btn) as AppCompatImageView
        val submitBtn = view.findViewById(R.id.submit_btn) as TextView

        builder.setCancelable(true)
        val alertDialog = builder.create()
        alertDialog.show()
        closeBtn.setOnClickListener { alertDialog.dismiss() }

        submitBtn.setOnClickListener {
            viewModel.raiseAnIssue("Bearer $token",
                RaiseAnIssueQueryParams(itemId,
                    "evaluation",
                    issueTitle.text.toString(),
                    issueReason.text.toString(),
                    2),Utility.getLanguage())
                .observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                scrollView.snack(resource.data?.MESSAGE!!)
                                alertDialog.dismiss()
                            }
                            Status.LOADING -> {

                            }
                            Status.ERROR -> {
//                                scrollView.snack(resource.data?.MESSAGE!!)
                                alertDialog.dismiss()
                            }
                        }
                    }
                })
        }
    }

    private fun returnDevice(requestId: Int) {
        viewModel.returnDeviceShipment(requestId, "Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA.let { it ->
                            progress_bar.makeGone()
                            scrollView.snack(resource.data!!.MESSAGE)
                            val fragment = PaymentSlipFragment()
                            val bundle = Bundle()
                            bundle.putString("paymentSlip", it!!.bill_sticker_pdf)
                            (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
                        }
                    }
                    Status.ERROR -> {
                        progress_bar.makeGone()
                    }
                    Status.LOADING -> {
                        progress_bar.makeVisible()
                    }
                }
            }
        })
    }

    private fun showReEvaluationDialog(requestId: Int) {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.re_evaluation_dialog, scrollView.findViewById(R.id.content), false)
        builder.setView(view)

        val amount = view.findViewById(R.id.amount_edit_text) as AppCompatEditText
        val reason = view.findViewById(R.id.reason_edit_text) as AppCompatEditText
        val closeBtn = view.findViewById(R.id.close_btn) as AppCompatImageView
        val submitBtn = view.findViewById(R.id.submit_btn) as TextView

        builder.setCancelable(true)
        val alertDialog = builder.create()
        alertDialog.show()
        closeBtn.setOnClickListener { alertDialog.dismiss() }

        submitBtn.setOnClickListener {
            val map = HashMap<String, String>()
            map["amount"] = amount.text.toString()
            map["reason"] = reason.text.toString()

            viewModel.reEvaluateRequest(requestId, "Bearer $token", map,Utility.getLanguage())
                .observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data.let { it ->
                                    alertDialog.dismiss()
                                    scrollView.snack(it!!.MESSAGE)
                                }
                            }
                            Status.LOADING -> {
                            }
                            Status.ERROR -> {
                                alertDialog.dismiss()
                                scrollView.snack(it.message.toString())
                            }
                        }
                    }
                })
        }
    }
}