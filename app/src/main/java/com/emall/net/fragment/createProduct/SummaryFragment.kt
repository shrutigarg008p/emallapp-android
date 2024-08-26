package com.emall.net.fragment.createProduct

import android.app.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.SummaryAdapter
import com.emall.net.model.QuestionAnswer
import com.emall.net.network.model.AddProduct
import com.emall.net.utils.Constants.PRODUCT
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import kotlinx.android.synthetic.main.fragment_submit_auction_evaluation.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SummaryFragment : Fragment(), View.OnClickListener {

    private lateinit var summaryAdapter: SummaryAdapter
    private val summaryList = ArrayList<QuestionAnswer>()

    private var addProduct: AddProduct? = null

    private var imagePathList = HashMap<String, String>()
    private var questionAnswerList = HashMap<String, String>()
    private var answerList = HashMap<Int, String>()
    private var imageList = arrayOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_submit_auction_evaluation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        addProduct = arguments?.getParcelable(PRODUCT)
        imagePathList = arguments?.getSerializable("imagePath") as HashMap<String, String>
        answerList = arguments?.getSerializable("answersList") as HashMap<Int, String>
        questionAnswerList = arguments?.getSerializable("questionAnswer") as HashMap<String, String>

        imageList = ArrayList(imagePathList.values).toTypedArray()

        evaluation_btn.setOnClickListener(this::onClick)
        auction_btn.setOnClickListener(this::onClick)
//        pay_now_button.setOnClickListener(this)

        setUpData()
    }

    private fun setUpData() {
        summary_recycler_view.setHasFixedSize(true)
        summary_recycler_view.layoutManager = LinearLayoutManager(activity)
        summaryAdapter = SummaryAdapter(summaryList)
        summary_recycler_view.adapter = summaryAdapter
        prepareData()
    }

    private fun prepareData() {
        summaryList.clear()
        item_brand.text = addProduct?.brand
        item_model.text = addProduct?.model
        item_name.text = addProduct?.model
        item_color.text = addProduct?.variant
        item_storage.text = "64 GB"
        item_ram.text = "4 GB"

        for (item in questionAnswerList) {
            summaryList.add(QuestionAnswer(item.key, item.value))
        }
        summaryAdapter.notifyDataSetChanged()
//        summaryList.add(QuestionAnswer("Product Type:", "Used"))
//        summaryAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.evaluation_btn -> moveToSubmitFragment("evaluation")
            R.id.auction_btn -> moveToSubmitFragment("auction")
            /*        R.id.pay_now_button -> {
                        val fragment = PaymentFragment()
                        val bundle = Bundle()
                        bundle.putString("submitType", submitType)
                        bundle.putInt("productId", productId!!)
                        fragment.arguments = bundle
                        (activity as MainActivity?)?.replace(fragment)
                    }*/
        }
    }


    private fun moveToSubmitFragment(submitType: String) {
        if (submitType.equals("auction", true)) {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("Y-MM-dd")
            val datePicker = DatePickerDialog(
                requireContext(),
                { view, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    TimePickerDialog(
                        requireContext(),
                        { view, hours, mins ->
                            calendar.set(Calendar.HOUR_OF_DAY, hours)
                            calendar.set(Calendar.MINUTE, mins)
                            val selectedDate =
                                dateFormat.format(calendar.time) + " " + hours + ":" + mins

                            if (hours >= Calendar.getInstance().get(Calendar.HOUR_OF_DAY) && mins >= Calendar.getInstance().get(Calendar.MINUTE)) {
                                val fragment = SubmitFragment()
                                val bundle = Bundle()

                                Log.d("TAG", "moveToSubmitFragment: ")


                                bundle.putString("date", selectedDate)
                                bundle.putString("submit_type", submitType)
                                bundle.putParcelable(PRODUCT, addProduct)
                                bundle.putSerializable("answersList", answerList)
                                bundle.putSerializable("imagePath", imagePathList)
                                fragment.arguments = bundle
                                (activity as SellerActivity).replaceFragment(
                                    fragment,
                                    R.id.container
                                )
                            } else {
                                constraint_layout.snack("Past Date can not be selected")
                            }
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
            datePicker.show()
        } else {
            val fragment = SubmitFragment()
            val bundle = Bundle()
            bundle.putString("date", "selectedDate")
            bundle.putString("submit_type", submitType)
            bundle.putParcelable(PRODUCT, addProduct)
            bundle.putSerializable("answersList", answerList)
            bundle.putSerializable("imagePath", imagePathList)
            fragment.arguments = bundle
            (activity as SellerActivity).replaceFragment(fragment, R.id.container)
        }
    }
}