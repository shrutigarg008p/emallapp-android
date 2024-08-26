package com.emall.net.fragment.evaluationDevices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.AuctionDeviceImagesAdapter
import com.emall.net.adapter.SummaryAdapter
import com.emall.net.model.QuestionAnswer
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.*
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeInvisible
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.google.gson.JsonObject
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.evaluation_device_detail.*

class EvaluationDevicesDetailFragment: Fragment(), View.OnClickListener {

    private var token: String? = ""
    private lateinit var viewModel: MainViewModel
    private var evaluationId: Int? = null

    private lateinit var summaryAdapter: SummaryAdapter
    private lateinit var auctionDeviceImagesAdapter: AuctionDeviceImagesAdapter
    private val summaryList = ArrayList<QuestionAnswer>()
    private val imageList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.evaluation_device_detail, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)
        when {
            arguments != null -> {
                evaluationId = arguments?.getInt("evaluationId")
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)

        evaluation_btn.text = "Request For Evaluate"

        question_answer_recycler_view.setHasFixedSize(true)
        question_answer_recycler_view.layoutManager = LinearLayoutManager(activity)
        summaryAdapter = SummaryAdapter(summaryList)
        question_answer_recycler_view.adapter = summaryAdapter

        additional_images_list.setHasFixedSize(true)
        additional_images_list.layoutManager = GridLayoutManager(activity,3)
        auctionDeviceImagesAdapter = AuctionDeviceImagesAdapter(imageList)
        additional_images_list.adapter = auctionDeviceImagesAdapter

        amountHeading.text = "Evaluation Amount"
        evaluation_btn.setOnClickListener(this)

        viewModel.getEvaluationDevicesDetail(evaluationId!!,"Bearer $token",Utility.getLanguage()).observe(viewLifecycleOwner,{
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA?.let { it ->
                            progress_bar.makeGone()
                            item_name.text = it.product.name
                            item_brand.text = it.product.brand
                            item_model.text = it.product.model
                            item_color.text = it.product.attributes[0].value
                            item_ram.text = "4 GB"
                            item_storage.text = "64 GB"

                            if (it.product.qna != null) {
                                for (item in it.product.qna) {
                                    summaryList.add(QuestionAnswer(item.question, item.answer.toString()))
                                }
                                summaryAdapter.notifyDataSetChanged()
                            }

                            imageList.clear()
                            if (it.product.additional_images.isNotEmpty()) {
                                for (image in it.product.additional_images)
                                    if(image.isNotEmpty())
                                        imageList.add(image)
                                if(imageList.isNotEmpty())
                                    auctionDeviceImagesAdapter.notifyDataSetChanged()
                            }

                            bid_details_heading.text = it.extra_notes.note2
                            bid_warning.text = it.extra_notes.note2
                            Picasso.get().load(it.product.serial_no_img).into(serial_number_image)

                            if(it.evaluated_amount != null){
                                evaluated_amount.text = it.evaluated_amount.toString()
                                textView55.makeVisible()
                                amountHeading.makeVisible()
                                evaluated_amount.makeVisible()
                            }else{
                                evaluated_amount.text = it.evaluated_amount.toString()
                                textView55.makeInvisible()
                                amountHeading.makeInvisible()
                                evaluated_amount.makeInvisible()
                            }
                            bid_details_heading.makeGone()
                            bid_warning.makeGone()
                        }
                    }
                    Status.ERROR -> {
                        progress_bar.makeGone()
                        scrollView.snack(it.message!!)
                    }
                    Status.LOADING -> progress_bar.makeVisible()
                }
            }
        })
    }

    override fun onClick(v: View) {
        if(v.id == R.id.evaluation_btn){
            // get evaluation request api call
            val obj = JsonObject()
            obj.addProperty("amount",amount.text.toString().toInt())

            when {
                amount.text.toString().toInt() > 0 -> {
                    viewModel.addEvaluationAmount(evaluationId!!, "Bearer $token", obj,Utility.getLanguage()).observe(
                        viewLifecycleOwner, {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> {
                                        resource.data?.let { it ->
                                            progress_bar.makeGone()
                                            scrollView.snack(it.MESSAGE)
                                        }
                                    }
                                    Status.ERROR -> progress_bar.makeGone()
                                    Status.LOADING -> progress_bar.makeVisible()
                                }
                            }
                        }
                    )
                }
                else -> {
                    scrollView.snack("Please enter amount valid amount")
                }
            }
        }

    }


}