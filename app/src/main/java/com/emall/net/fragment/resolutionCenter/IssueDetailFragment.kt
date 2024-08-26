package com.emall.net.fragment.resolutionCenter

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.util.Util
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.network.api.*
import com.emall.net.network.model.getIssuesList.IssueListData
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_issue_detail.*

class IssueDetailFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel
    private var issueList = ArrayList<IssueListData>()
    private var token: String? = ""
    private var issueId: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_issue_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle("Issue Detail")
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle("Issue Detail")
            }
        }

        when {
            arguments != null -> issueId = arguments?.getInt("id")
        }

        chat_with_seller_btn.setOnClickListener(this)
        chat_with_admin_btn.setOnClickListener(this)
        close_btn_container.setOnClickListener(this)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        viewModel.getIssueDetail("Bearer $token", issueId!!,Utility.getLanguage()).observe(viewLifecycleOwner, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.let { it ->
                            issue_id_number.text = it.number
                            item_type.text = it.type_text
                            item_id.text = it.id.toString()
                            item_raised_at.text = it.raised_at.toString()
                            item_raised_by.text = it.raised_by
                            item_closed_at.text = it.closed_at.toString()
                            item_closed_by.text = it.closed_by.toString()
                            description_text.text = it.description
                            title_text.text = it.title
                            if (it.status_text.contains("close", true)) {
                                chat_with_admin_btn.makeGone()
                                chat_with_seller_btn.makeGone()
                                close_btn_container.makeGone()
                                closed_at_heading.makeVisible()
                                closed_by_heading.makeVisible()
                                item_closed_by.makeVisible()
                                item_closed_at.makeVisible()
                            } else {
                                chat_with_admin_btn.makeVisible()
                                chat_with_seller_btn.makeVisible()
                                close_btn_container.makeVisible()
                                closed_at_heading.makeGone()
                                closed_by_heading.makeGone()
                                item_closed_by.makeGone()
                                item_closed_at.makeGone()
                            }
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.chat_with_seller_btn -> {

            }
            R.id.chat_with_admin_btn -> {

            }
            R.id.close_btn_container -> {
                viewModel.closeAnIssue("Bearer $token", issueId!!,Utility.getLanguage()).observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { it ->
                                    constraint_layout.snack(it.MESSAGE)
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
        }
    }
}