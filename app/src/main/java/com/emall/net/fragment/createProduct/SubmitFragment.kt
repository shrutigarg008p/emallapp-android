package com.emall.net.fragment.createProduct

import android.os.Bundle
import android.view.*
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.*
import com.emall.net.network.model.AddProduct
import com.emall.net.utils.*
import com.emall.net.utils.Constants.IS_SELLER
import com.emall.net.utils.Constants.PRODUCT
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.popBackStack
import com.emall.net.utils.Utility.popBackStackInclusive
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_submit.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URLConnection
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SubmitFragment : BaseFragment(), View.OnClickListener {

    private var token: String? = ""
    private var addProduct: AddProduct? = null
    private lateinit var viewModel: MainViewModel

    private var imagePathList = HashMap<String, String>()
    private var answerList = HashMap<Int, String>()
    private var imageList = ArrayList<String>()

    private var date: String? = ""
    private var submitType: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_submit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        scopeMainThread.launch {
            date = arguments?.getString("date")
            submitType = arguments?.getString("submit_type")
            addProduct = arguments?.getParcelable(PRODUCT)
            imagePathList = arguments?.getSerializable("imagePath") as HashMap<String, String>
            answerList = arguments?.getSerializable("answersList") as HashMap<Int, String>

            for (values in imagePathList) {
                imageList.add(values.value)
            }
            token = getReCommerceToken()

            date_text_view.setOnClickListener(this@SubmitFragment as View.OnClickListener)
            viewModel = getReCommerceViewModel()

            if (submitType.equals("auction", true)) {
                evaluation_btn.makeGone()
                auction_btn.makeVisible()
                date_text_view.makeVisible()
            } else {
                evaluation_btn.makeVisible()
                auction_btn.makeGone()
                date = String.format("%1tY-%<tm-%<td %<tR", Calendar.getInstance())
                date_text_view.makeGone()
            }
            date_text_view.text = date
            evaluation_btn.setOnClickListener(this@SubmitFragment as View.OnClickListener)
            auction_btn.setOnClickListener(this@SubmitFragment as View.OnClickListener)
        }
    }

    private fun addProductToCategory(submitType: String) {

        // additional images process
        val multiPartList = ArrayList<MultipartBody.Part>()
        for (i in 0 until imageList.size) {
            val filePath = File(imageList[i])
            val mimeType = URLConnection.guessContentTypeFromName(filePath.name)
            val requestFile = RequestBody.create(mimeType.toMediaType(), filePath)
            val body =
                MultipartBody.Part.createFormData("additional_images[]", filePath.name, requestFile)
            multiPartList.add(body)
        }
        val fileArray: Array<MultipartBody.Part> =
            multiPartList.toArray(arrayOfNulls<MultipartBody.Part>(multiPartList.size))

        // serial number image process
        val serialNumberImageFilePath = File(addProduct?.serialNumberImage!!.toString())
        val mimeType = URLConnection.guessContentTypeFromName(serialNumberImageFilePath.name)
        val serialNumberRequestBody =
            RequestBody.create(mimeType.toMediaType(), serialNumberImageFilePath)
        val serialNumberImage = MultipartBody.Part.createFormData(
            "serial_no_img",
            serialNumberImageFilePath.name,
            serialNumberRequestBody
        )

        val params = HashMap<String, RequestBody>()
        for (answer in answerList) {
            params["cat_question_answer_${answer.key}"] = createRequestBody(answer.value)
        }

        viewModel.addProductToCategory(
            Utility.getLanguage(),
            addProduct?.categoryId!!,
            "Bearer $token",
            submitType.toRequestBody("text/plain".toMediaType()),
            addProduct?.brandId.toString().toRequestBody("text/plain".toMediaType()),
            addProduct?.modelId.toString().toRequestBody("text/plain".toMediaType()),
            addProduct?.variantId.toString().toRequestBody("text/plain".toMediaType()),
            fileArray,
            addProduct?.serialNo.toString().toRequestBody("text/plain".toMediaType()),
            serialNumberImage,
            addProduct?.description!!.toRequestBody("text/plain".toMediaType()),
            addProduct?.shortDescription!!.toRequestBody("text/plain".toMediaType()),
            params,
            date!!.toRequestBody("text/plain.".toMediaType())
        )
            .observe(viewLifecycleOwner,
                {
                    it?.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.DATA?.let { it ->
                                    progress_bar.makeGone()
                                    constraint_layout.snack("$submitType created")
                                    scopeMainThread.launch { delay(1000) }
                                    if (submitType.equals("auction", true)) {
                                        PreferenceHelper.writeBoolean(IS_SELLER, true)
                                        PreferenceHelper.writeString("product_activity", "auction")
                                    } else {
                                        PreferenceHelper.writeBoolean(IS_SELLER, false)
                                        PreferenceHelper.writeString(
                                            "product_activity",
                                            "evaluation"
                                        )
                                    }
                                    (activity as SellerActivity).moveToProductFragment()
                                }
                            }
                            Status.ERROR -> progress_bar.makeGone()
                            Status.LOADING -> progress_bar.makeVisible()
                        }
                    }

                })
    }

    private fun createRequestBody(s: String): RequestBody {
        return RequestBody.create("multipart/form-data".toMediaType(), s)
    }

    override fun onClick(v: View) {
        scopeMainThread.launch {
            when (v.id) {
                R.id.evaluation_btn -> addProductToCategory("evaluation")
                R.id.auction_btn -> addProductToCategory("auction")
            }
        }
    }
}