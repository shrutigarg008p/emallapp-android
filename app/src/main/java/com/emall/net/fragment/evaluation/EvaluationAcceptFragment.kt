package com.emall.net.fragment.evaluation

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.EvaluationAcceptAdapter
import com.emall.net.fragment.addressList.SellerAddressListFragment
import com.emall.net.fragment.bankDetails.UpdateBankDetailsFragment
import com.emall.net.fragment.payment.*
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.RaiseAnIssueQueryParams
import com.emall.net.network.api.*
import com.emall.net.network.model.evaluationAcceptDetail.*
import com.emall.net.network.model.shipmentList.DataX
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_bidding.*
import kotlinx.android.synthetic.main.fragment_evaluation_accept.*
import kotlinx.android.synthetic.main.fragment_evaluation_accept.chat_with_evaluator_btn
import kotlinx.android.synthetic.main.fragment_evaluation_accept.close_btn_container
import kotlinx.android.synthetic.main.fragment_evaluation_accept.constraintLayout
import kotlinx.android.synthetic.main.fragment_evaluation_accept.payment_heading
import kotlinx.android.synthetic.main.fragment_evaluation_accept.progress_bar
import kotlinx.android.synthetic.main.fragment_evaluation_accept.raise_an_issue_btn
import kotlinx.android.synthetic.main.fragment_evaluation_accept.raise_issue_card_view
import kotlinx.android.synthetic.main.fragment_evaluation_accept.scrollView
import kotlinx.android.synthetic.main.fragment_evaluation_accept.status
import kotlinx.android.synthetic.main.fragment_evaluation_accept.view_payment_slip
import kotlinx.android.synthetic.main.fragment_evaluation_accept.view_waybill
import java.util.*
import kotlin.collections.ArrayList

class EvaluationAcceptFragment : Fragment(), OnItemClick, View.OnClickListener {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var itemName: String? = ""
    private var productId: Int? = 0
    private var evaluationId: Int? = 0
    private var shipmentId: Int? = 0
    private lateinit var evaluationAcceptAdapter: EvaluationAcceptAdapter
    private var evaluationAcceptList = ArrayList<Evaluation>()
    private var evaluationTimeOver: String? = ""
    private var paymentSlip: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_evaluation_accept, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)!!
        when {
            arguments != null -> {
                evaluationId = arguments?.getInt("evaluationId")
                productId = arguments?.getInt(Constants.PRODUCT_ID)
                itemName = arguments?.getString("name")
            }
        }

        raise_an_issue_btn.setOnClickListener(this)
        chat_with_evaluator_btn.setOnClickListener(this)
        close_btn_container.setOnClickListener(this)
        evaluation_name_heading.text = itemName

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        evaluation_recycler_view.setHasFixedSize(true)
        evaluation_recycler_view.layoutManager = LinearLayoutManager(activity)
        evaluationAcceptAdapter = EvaluationAcceptAdapter(evaluationAcceptList, this)
        evaluation_recycler_view.adapter = evaluationAcceptAdapter
        view_waybill.setOnClickListener(this)
        view_payment_slip.setOnClickListener(this)
        evaluation_time.setOnClickListener(this)
        viewModel.getAcceptedEvaluationDetail(productId!!, evaluationId!!, "Bearer $token",Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA.let { it ->

                                /*

                                if(it.abandoned_message.toString().isNotEmpty()){
                                    when(it.abandoned_message){
                                        is AbandonedMessage ->{
                                            payment_heading.makeVisible()
                                            raise_issue_card_view.makeVisible()
                                        }else ->{
                                        payment_heading.makeGone()
                                        raise_issue_card_view.makeGone()
                                        }
                                    }
                                }
                                 */
                                progress_bar.makeGone()
                                evaluationAcceptList.clear()

                                evaluation_date.text = it!!.starts_at
                                evaluation_time.text = it.starts_at_str
                                status.text = it.current_status
                                evaluation_id.text = it.id.toString()

                                evaluationTimeOver = it.ends_at_str
                                evaluationAcceptList.addAll(it.evaluations)
                                evaluationAcceptAdapter.notifyDataSetChanged()
                                if (it.shipment != null) {
                                    shipmentId = it.shipment.id
                                    view_waybill.makeVisible()
                                } else {
                                    view_waybill.makeGone()
                                }

                                if (it.order != null) {
                                    view_payment_slip.makeVisible()
                                    paymentSlip = it.order.payment_slip
                                } else {
                                    view_payment_slip.makeGone()
                                }

                                if (it.evaluations.size == 0)
                                    no_evaluation_heading.makeVisible()
                                else
                                    no_evaluation_heading.makeGone()

                                if (it.abandoned_message.toString().isNotEmpty()) {
                                    when (it.abandoned_message) {
                                        is AbandonedMessage -> {
                                            payment_heading.makeVisible()
                                            raise_issue_card_view.makeVisible()
                                        }
                                        else -> {
                                            payment_heading.makeGone()
                                            raise_issue_card_view.makeGone()
                                        }
                                    }
                                }
                            }
                        }
                        Status.LOADING -> progress_bar.makeVisible()
                        Status.ERROR -> {
                            progress_bar.makeGone()
                            Log.d("TAG", "callBiddingAPI: " + it.data?.MESSAGE)
                            constraintLayout.snack("Error")

                        }
                    }
                }
            })
        if (String.format("%1tY-%<tm-%<td %<tR", Calendar.getInstance()) > evaluationTimeOver!!) {
            PreferenceHelper.writeBoolean("evaluationDate", true)
        }
    }

    override fun onItemClick(position: Int, type: String) {
        // move to bank fragment
        when {
            type.contentEquals("Accept Evaluation") -> {
                viewModel.acceptEvaluation(evaluationAcceptList[position].id, "Bearer $token",Utility.getLanguage())
                    .observe(
                        viewLifecycleOwner,
                        {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> {
                                        resource.data?.DATA.let { it ->
                                            // open payment fragment
                                            progress_bar.makeGone()
                                            scrollView.snack("Evaluation Accepted")
                                            moveToPaymentFragment(position)
                                        }
                                    }
                                    Status.ERROR -> progress_bar.makeGone()
                                    Status.LOADING -> progress_bar.makeVisible()
                                }
                            }

                        })
            }
            type.contentEquals("add bank details") -> (activity as SellerActivity).replaceFragment(
                UpdateBankDetailsFragment(),
                R.id.container
            )
            type.contentEquals("stc payment") -> moveToPaymentFragment(position)
            type.contentEquals("add address") -> {
                val fragment = SellerAddressListFragment()
                val bundle = Bundle()
                PreferenceHelper.writeInt(Constants.PRODUCT_ID, productId!!)
                fragment.arguments = bundle
                (activity as SellerActivity).replaceFragment(fragment, R.id.container)
            }
            type.contentEquals("reject") -> {
                acceptRejectEvaluation("reject", evaluationAcceptList[position].id)
            }
            type.contentEquals("Accept Re-Evaluation") -> {
                acceptRejectEvaluation("accept", evaluationAcceptList[position].id)
            }
            else -> {
            }
        }
    }

    private fun acceptRejectEvaluation(action: String, evaluationId: Int) {
        viewModel.acceptRejectAnEvaluation(evaluationId, action, "Bearer $token",Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let { it ->
                                progress_bar.makeGone()
                                scrollView.snack(it.MESSAGE)
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

    private fun moveToPaymentFragment(position: Int) {
        val fragment = PaymentFragment()
        val bundle = Bundle()
        bundle.putInt(Constants.PRODUCT_ID, productId!!)
        bundle.putInt("evaluationId", evaluationId!!)
        bundle.putDouble("amount", evaluationAcceptList[position].amount.toDouble())
        fragment.arguments = bundle
        (activity as SellerActivity).replaceFragment(fragment, R.id.container)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.view_waybill -> {
                viewModel.getShipmentList("Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.DATA?.data.let { it ->
                                    moveToShipmentFragment(it)
                                }
                            }
                            Status.LOADING -> {

                            }
                            Status.ERROR -> {

                            }
                        }
                    }
                })
            }
            R.id.view_payment_slip -> {
                val fragment = PaymentSlipFragment()
                val bundle = Bundle()
                bundle.putString("paymentSlip", paymentSlip!!)
                fragment.arguments = bundle
                (activity as SellerActivity).replaceFragment(fragment, R.id.container)
            }
            R.id.chat_with_evaluator_btn -> {
                // move to chat
            }
            R.id.raise_an_issue_btn -> {
                raiseIssueDialog()
            }
        }
    }

    private fun raiseIssueDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.resolution_center_dialog,
                scrollView.findViewById(R.id.content),
                false
            )
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
            viewModel.raiseAnIssue(
                "Bearer $token",
                RaiseAnIssueQueryParams(
                    evaluationId!!,
                    "evaluation",
                    issueTitle.text.toString(),
                    issueReason.text.toString(),
                    3
                ),Utility.getLanguage()
            )
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
                                scrollView.snack(resource.data?.MESSAGE!!)
                                alertDialog.dismiss()
                            }
                        }
                    }
                })
        }
    }

    private fun moveToShipmentFragment(info: java.util.ArrayList<DataX>?) {
        var addressId = 0
        for (shipment in info!!) {
            if (shipmentId!! == shipment.id) {
                addressId = shipment.ship_from
            }
        }

        val fragment = ShipmentFragment()
        val bundle = Bundle()
        bundle.putBoolean("waybill", true)
        bundle.putInt("addressId", addressId)
        bundle.putInt("shipmentId", shipmentId!!)
        PreferenceHelper.writeInt(Constants.PRODUCT_ID, productId!!)
        fragment.arguments = bundle
        (activity as SellerActivity).replaceFragment(fragment, R.id.container)
    }

}