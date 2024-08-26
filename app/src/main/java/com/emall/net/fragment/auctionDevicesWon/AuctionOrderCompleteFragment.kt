package com.emall.net.fragment.auctionDevicesWon

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.util.Util
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.*
import com.emall.net.utils.Constants.SHIPMENT_ID
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.popBackStackInclusive
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_order_complete.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URLConnection

class AuctionOrderCompleteFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var auctionWonId: Int? = null
    private var amount: Double? = null
    private var shippingId: Int? = null
    private var pdf: MultipartBody.Part? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_order_complete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        when {
            arguments != null -> {
                auctionWonId = arguments?.getInt("auctionWonId")
                shippingId = arguments?.getInt(SHIPMENT_ID)
            }
        }

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        viewModel.auctionDevicesWon(auctionWonId!!, "Bearer $token",Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA.let { it ->
                                progress_bar.makeGone()
                                userName.text = it!!.seller.name
                                userAccountNumber.text = it.seller.bank.account_number
                                bank_name.text = it.seller.bank.bank_name
                                mobile_number.text = it.seller.bank.account_number
                                amount = it.amount.toDouble()
                            }
                        }
                        Status.LOADING -> progress_bar.makeVisible()
                        Status.ERROR -> progress_bar.makeGone()
                    }
                }
            })

        choose_file_btn.setOnClickListener {
            val browseStorage = Intent(Intent.ACTION_GET_CONTENT)
            browseStorage.type = "application/pdf"
            browseStorage.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(Intent.createChooser(browseStorage, "Select PDF"), 111)
        }

        upload_file_receipt_btn.setOnClickListener {
            viewModel.auctionDeviceWonPayment(auctionWonId!!, "Bearer $token",
                shippingId!!.toInt().toString().toRequestBody("text/plain".toMediaType()),
                pdf!!,
                amount!!.toString().toRequestBody("text/plain".toMediaType()),
                Utility.getLanguage()
            )
                .observe(viewLifecycleOwner, {
                    it.let { resource ->
                        when (resource.status) {
                            Status.SUCCESS -> {
                                resource.data?.let { it ->
                                    progress_bar.makeGone()
                                    constraint_layout.snack(it.MESSAGE)
                                    (activity as BuyerActivity).popBackStackInclusive()
                                }
                            }
                            Status.LOADING -> progress_bar.makeVisible()
                            Status.ERROR -> {
                                progress_bar.makeGone()
                            }
                        }
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            RESULT_OK -> when (requestCode) {
                111 -> {
                    val filePath = data!!.data
                    val type = File(copyFileToInternalStorage(filePath!!, "emall"))
                    chosen_file.text = type.name
                    val mimeType = URLConnection.guessContentTypeFromName(type.name)
                    val requestBody = type.asRequestBody(mimeType.toMediaType())

                    pdf = MultipartBody.Part.createFormData("offline_payment_slip",
                        type.name,
                        requestBody)
                }
            }
        }
    }

    private fun copyFileToInternalStorage(uri: Uri, newDirName: String): String? {
        val returnCursor: Cursor = requireContext().contentResolver.query(uri, arrayOf(
            OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        ), null, null, null)!!


        /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val output: File
        if (newDirName != "") {
            val dir: File = File(requireContext().filesDir.toString() + "/" + newDirName)
            if (!dir.exists()) {
                dir.mkdir()
            }
            output = File(requireContext().filesDir.toString() + "/" + newDirName + "/" + name)
        } else {
            output = File(requireContext().filesDir.toString() + "/" + name)
        }
        try {
            val inputStream: InputStream = requireContext().contentResolver.openInputStream(uri)!!
            val outputStream = FileOutputStream(output)
            var read = 0
            val bufferSize = 1024
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
        }
        return output.path
    }
}