package com.emall.net.fragment.chat

import android.os.Bundle
import android.view.*
import android.view.View.OnClickListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import com.emall.net.R
import com.emall.net.adapter.ChatAdapter
import com.emall.net.network.api.*
import com.emall.net.network.model.chat.SendMessage
import com.emall.net.network.model.chat.viewChannelMessages.Message
import com.emall.net.utils.*
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_chat_messages.*
import org.json.JSONObject

class ChatMessagesFragment : Fragment(), OnClickListener {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var channelId: Int? = 0
    private val messageList = ArrayList<Message>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.fragment_chat_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        when {
            arguments != null -> channelId = arguments?.getInt("channelId")
        }

        send_btn.setOnClickListener(this)

        chat_recycler_view.setHasFixedSize(true)
        chat_recycler_view.layoutManager = LinearLayoutManager(activity)
        chatAdapter = ChatAdapter(messageList)
        chat_recycler_view.adapter = chatAdapter

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        viewModel.getChannelMessages("Bearer $token", channelId!!,Utility.getLanguage()).observe(viewLifecycleOwner, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.let { it ->
                            messageList.clear()
                            messageList.addAll(it.messages)
                            chatAdapter.notifyDataSetChanged()
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
            R.id.send_btn -> createChannel()
        }
    }

    private fun createChannel() {
        val jsonObject = JSONObject()
        val intArray = arrayOf(2)
        jsonObject.put("users", intArray)
        viewModel.createChannel("Bearer $token", jsonObject,Utility.getLanguage()).observe(viewLifecycleOwner, {
            it.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.let { it ->
                            channelId = it.channel_id
                            sendMessage()
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

    private fun sendMessage() {
        viewModel.sendMessage("Bearer $token", channelId!!, SendMessage(message.text.toString()),Utility.getLanguage())
            .observe(viewLifecycleOwner, {
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