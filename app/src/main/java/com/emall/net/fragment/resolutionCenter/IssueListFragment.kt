package com.emall.net.fragment.resolutionCenter

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.IssueAdapter
import com.emall.net.fragment.chat.ChannelFragment
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.*
import com.emall.net.network.model.getIssuesList.IssueListData
import com.emall.net.utils.*
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_issue_list.*

class IssueListFragment : Fragment(), OnItemClick {

    private lateinit var issueAdapter: IssueAdapter
    private lateinit var viewModel: MainViewModel
    private var issueList = ArrayList<IssueListData>()
    private var token: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_issue_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)!!

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle("Issue List")
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle("Issue List")
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        issue_recycler_view.setHasFixedSize(true)
        issue_recycler_view.layoutManager = LinearLayoutManager(activity)
        issueAdapter = IssueAdapter(issueList, this)
        issue_recycler_view.adapter = issueAdapter

        viewModel.getIssueList("Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.data.let { it ->
                            if(it!!.isEmpty()){
                                constraint_layout.snack(getString(R.string.no_issue_found))
                            }else {
                                issueList.clear()
                                issueList.addAll(it)
                                issueAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                    Status.LOADING -> {

                    }
                    Status.ERROR -> {
                        Log.d("TAG", "onViewCreated: " +it.message)
                    }
                }
            }
        })
    }

    override fun onItemClick(position: Int, type: String) {
        when {
            type.equals("issue detail", true) -> {
                val fragment = IssueDetailFragment()
                val bundle = Bundle()
                bundle.putInt("id",issueList[position].id)
                fragment.arguments = bundle
                when(activity) {
                    is SellerActivity -> (activity as SellerActivity).replaceFragment(
                        fragment,
                        R.id.container
                    )
                    else -> (activity as BuyerActivity).replaceFragment(fragment, R.id.container)
                }
            }
            type.equals("seller chat", true) -> {
                // move to chat
                when (activity) {
                    is SellerActivity -> (activity as SellerActivity).replaceFragment(
                        ChannelFragment(),
                        R.id.container
                    )
                    else -> (activity as BuyerActivity).replaceFragment(
                        ChannelFragment(),
                        R.id.container
                    )
                }
            }
            type.equals("admin chat", true) -> {
                // move to chat
                when (activity) {
                    is SellerActivity -> (activity as SellerActivity).replaceFragment(
                        ChannelFragment(),
                        R.id.container
                    )
                    else -> (activity as BuyerActivity).replaceFragment(
                        ChannelFragment(),
                        R.id.container
                    )
                }

            }
            type.equals("close issue", true) -> closeIssue(issueList[position].id)
        }
    }

    private fun closeIssue(issueId:Int) {
        viewModel.closeAnIssue("Bearer $token",issueId,Utility.getLanguage()).observe(viewLifecycleOwner,{
            it.let {resource ->
            when(resource.status){
                Status.SUCCESS ->{
                    resource.data.let { it ->
                        constraint_layout.snack(it!!.MESSAGE)
                    }
                }Status.LOADING ->{

                }Status.ERROR ->{

                }
            }
            }
        })
    }
}