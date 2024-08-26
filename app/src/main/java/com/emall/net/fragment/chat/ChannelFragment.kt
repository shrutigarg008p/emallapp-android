package com.emall.net.fragment.chat

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.adapter.ChannelAdapter
import com.emall.net.listeners.ItemClick
import com.emall.net.network.api.*
import com.emall.net.network.model.chat.channelList.Channel
import com.emall.net.utils.*
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_channel.*

class ChannelFragment : Fragment(), ItemClick {

    private lateinit var channelAdapter: ChannelAdapter
    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var channelList = ArrayList<Channel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_channel,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        channel_recycler_view.setHasFixedSize(true)
        channel_recycler_view.layoutManager = LinearLayoutManager(activity)
        channelAdapter = ChannelAdapter(channelList,this)
        channel_recycler_view.adapter = channelAdapter

        viewModel.getChannelList("Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner,{
            it.let { resource ->
                when(resource.status){
                    Status.SUCCESS ->{
                        resource.data?.DATA?.data.let {it ->
                            channelList.clear()
                            channelList.addAll(it!!)
                            channelAdapter.notifyDataSetChanged()
                        }
                    }Status.LOADING ->{

                    }Status.ERROR ->{

                    }
                }
            }
        })
    }

    override fun itemClick(position: Int) {
        // move to channel messages
        val fragment = ChatMessagesFragment()
        val bundle = Bundle()
        bundle.putInt("channelId",channelList[position].id)
        fragment.arguments = bundle
        when(activity){
            is SellerActivity -> (activity as SellerActivity).replaceFragment(fragment,R.id.container)
            else -> (activity as BuyerActivity).replaceFragment(fragment,R.id.container)
        }
    }
}