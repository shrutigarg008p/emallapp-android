package com.emall.net.fragment.auctionDevicesWon

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
import com.bumptech.glide.util.Util
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.AuctionDevicesWonAdapter
import com.emall.net.fragment.addressList.EvaluatorAddressListFragment
import com.emall.net.fragment.resolutionCenter.IssueDetailFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.model.RaiseAnIssueQueryParams
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.auctionDevicesWon.AuctionDevicesWon
import com.emall.net.utils.*
import com.emall.net.utils.Constants.SHIPMENT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_product.*

class AuctionDevicesWonFragment : Fragment(), OnItemClick {

    private lateinit var auctionDevicesWonAdapter: AuctionDevicesWonAdapter
    private val auctionDevicesWonList = ArrayList<AuctionDevicesWon>()

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        auctionDevicesWonAdapter = AuctionDevicesWonAdapter(auctionDevicesWonList, this)
        auction_recycler_view.adapter = auctionDevicesWonAdapter

        viewModel.getAuctionDevicesWon("Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.data.let {
                            progress_bar.makeGone()
                            auctionDevicesWonList.clear()
                            auctionDevicesWonList.addAll(it!!)
                            auctionDevicesWonAdapter.notifyDataSetChanged()
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
                bundle.putInt("auctionWonId", auctionDevicesWonList[position].id)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment,R.id.container)
            }
            type.equals("raiseIssue", true) -> {
                // raise issue
                raiseIssueDialog(auctionDevicesWonList[position].id)
            }type.equals("view issue",true) ->{
            // view issue
            val fragment = IssueDetailFragment()
            val bundle = Bundle()
            bundle.putInt("id",auctionDevicesWonList[position].raise_issue.issue.id)
            fragment.arguments = bundle
            (activity as BuyerActivity).replaceFragment(fragment,R.id.container)
        }

            else -> {
                // proceed to offline payment
                val fragment = AuctionOrderCompleteFragment()
                val bundle = Bundle()
                bundle.putInt("auctionWonId", auctionDevicesWonList[position].id)
                bundle.putInt(SHIPMENT_ID, auctionDevicesWonList[position].shipping_id)
                fragment.arguments = bundle
                (activity as BuyerActivity).replaceFragment(fragment,R.id.container)
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
                                scrollView.snack(resource.data?.MESSAGE!!)
                                alertDialog.dismiss()
                            }
                        }
                    }
                })
        }
    }

}