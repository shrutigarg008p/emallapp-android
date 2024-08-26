package com.emall.net.fragment.createProduct

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.*
import com.emall.net.network.model.AddProduct
import com.emall.net.network.model.createdProduct.Question
import com.emall.net.utils.*
import com.emall.net.utils.Constants.BACK
import com.emall.net.utils.Constants.BACK_IMAGE
import com.emall.net.utils.Constants.BOTTOM
import com.emall.net.utils.Constants.BOTTOM_IMAGE
import com.emall.net.utils.Constants.FRONT
import com.emall.net.utils.Constants.FRONT_IMAGE
import com.emall.net.utils.Constants.LEFT
import com.emall.net.utils.Constants.LEFT_IMAGE
import com.emall.net.utils.Constants.PRODUCT
import com.emall.net.utils.Constants.QUESTION_LIST
import com.emall.net.utils.Constants.RIGHT
import com.emall.net.utils.Constants.RIGHT_IMAGE
import com.emall.net.utils.Constants.SELLER_EVALUATOR_TOKEN
import com.emall.net.utils.Constants.SERIAL_NUMBER
import com.emall.net.utils.Constants.SERIAL_NUMBER_DATA
import com.emall.net.utils.Constants.TOP
import com.emall.net.utils.Constants.TOP_IMAGE
import com.emall.net.utils.Utility.addFragment
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_serial_number.*
import java.io.ByteArrayOutputStream
import kotlin.collections.set


class SerialNumberFragment : Fragment(), View.OnClickListener {

    private var token: String? = ""
    private lateinit var viewModel: MainViewModel
    private var addProduct: AddProduct? = null
    private var questionList = ArrayList<Question>()

    private var imageType: String = ""
    private var imagePathList = HashMap<Int, String>()
    private val TAG = SerialNumberFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_serial_number, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))

        addProduct = arguments?.getParcelable(PRODUCT)
        token = PreferenceHelper.readString(SELLER_EVALUATOR_TOKEN)!!

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        add_serial_number_btn.setOnClickListener(this::onClick)
        left_image.setOnClickListener(this::onClick)
        right_image.setOnClickListener(this::onClick)
        top_image.setOnClickListener(this::onClick)
        bottom_image.setOnClickListener(this::onClick)
        front_image.setOnClickListener(this::onClick)
        back_image.setOnClickListener(this::onClick)
        continue_btn.setOnClickListener(this::onClick)
        click_here.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.add_serial_number_btn -> {
                imageType = SERIAL_NUMBER
                uploadImage()
            }
            R.id.left_image -> {
                imageType = LEFT
                uploadImage()
            }
            R.id.right_image -> {
                imageType = RIGHT
                uploadImage()
            }
            R.id.top_image -> {
                imageType = TOP
                uploadImage()
            }
            R.id.bottom_image -> {
                imageType = BOTTOM
                uploadImage()
            }
            R.id.front_image -> {
                imageType = FRONT
                uploadImage()
            }
            R.id.back_image -> {
                imageType = BACK
                uploadImage()
            }
            R.id.continue_btn -> createProduct()
            R.id.click_here -> showSerialNumberWebView()
        }
    }

    private fun showSerialNumberWebView() {
        viewModel.showSerialNumberPage("Bearer $token",Utility.getLanguage(),addProduct!!.brandId).observe(viewLifecycleOwner,{
            it.let {resource ->
            when(resource.status){
                Status.SUCCESS ->{
                    it.data?.DATA.let { it ->
                        val fragment = SerialNumberWebViewFragment()
                        val bundle = Bundle()
                        bundle.putString(SERIAL_NUMBER_DATA,it!!)
                        fragment.arguments = bundle
                        (activity as SellerActivity).addFragment(fragment,R.id.container)
                    }
                }Status.LOADING ->{

                }Status.ERROR ->{
                    scrollView.snack(it.data?.MESSAGE!!)
                }
            }
            }
        })
    }

    private fun uploadImage() {
        val options = arrayOf<CharSequence>(getString(R.string.take_photo), getString(R.string.choose_from_gallery), getString(R.string.cancel))
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.choose_picture))
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == getString(R.string.take_photo) -> {
                    requestPermission.launch(android.Manifest.permission.CAMERA)
                }
                options[item] == getString(R.string.choose_from_gallery) -> getContent.launch("image/*")
                else -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            val path = Utility.getPath(requireContext(), it)
            Log.d("TAG", " file name = $path")
            Log.d(TAG, "image type =  $imageType")
            showImages(it)
        }
    }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val bitmap = result.data?.extras?.get("data") as Bitmap

                    Log.d(TAG, ":dfsdf $bitmap")
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes)
                    val path = MediaStore.Images.Media.insertImage(context?.contentResolver,bitmap,"myphoto",null)
                    Picasso.get().load(Uri.parse(path)).into(serialCardImage)
                    showImages(Uri.parse(path))
                }
            }
        }

    private var requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->
        if(isGranted){
            val takePicture = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            launchSomeActivity.launch(takePicture)
        }else{
            scrollView.snack(getString(R.string.permission_denied))
        }
    }

    private fun showImages(it: Uri?) {
        when (imageType) {
            SERIAL_NUMBER -> setImageUri(it, serialCardImage, imageType)
            TOP -> setImageUri(it, top_image, imageType)
            BOTTOM -> setImageUri(it, bottom_image, imageType)
            RIGHT -> setImageUri(it, right_image, imageType)
            LEFT -> setImageUri(it, left_image, imageType)
            FRONT -> setImageUri(it, front_image, imageType)
            else -> setImageUri(it, back_image, imageType)
        }
    }

    private fun setImageUri(uri: Uri?, imageView: AppCompatImageView, type: String) {
        Picasso.get().load(uri).into(imageView)
        addImagesToList(uri, type)
    }

    private fun addImagesToList(uri: Uri?, type: String) {
        when (type) {
            LEFT -> imagePathList[LEFT_IMAGE] = Utility.getPath(requireContext(), uri!!)!!
            RIGHT -> imagePathList[RIGHT_IMAGE] = Utility.getPath(requireContext(), uri!!)!!
            TOP -> imagePathList[TOP_IMAGE] = Utility.getPath(requireContext(), uri!!)!!
            BOTTOM -> imagePathList[BOTTOM_IMAGE] = Utility.getPath(requireContext(), uri!!)!!
            FRONT -> imagePathList[FRONT_IMAGE] = Utility.getPath(requireContext(), uri!!)!!
            BACK -> imagePathList[BACK_IMAGE] = Utility.getPath(requireContext(), uri!!)!!
            else -> addProduct?.serialNumberImage = Utility.getPath(requireContext(), uri!!)!!
        }
    }

    private fun createProduct() {
        if(addProduct?.serialNumberImage.isNullOrEmpty()){
            scrollView.snack("Please select serial number image")
        }else {
            addProduct?.serialNo = serial_number_input.text.toString()
            viewModel.createProduct(
                addProduct?.categoryId!!,
                "Bearer $token",
                Utility.getLanguage()
            )
                .observe(viewLifecycleOwner,
                    Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    questionList.clear()
                                    resource.data?.DATA?.questions.let { list ->
                                        progress_bar.makeGone()
                                        val fragment = QuestionFragment()
                                        val bundle = Bundle()
                                        questionList = list!!
                                        bundle.putSerializable("imagePath", imagePathList)
                                        bundle.putParcelableArrayList(
                                            QUESTION_LIST,
                                            questionList
                                        )
                                        bundle.putParcelable(PRODUCT, addProduct)
                                        fragment.arguments = bundle
                                        (activity as SellerActivity?)?.replaceFragment(
                                            fragment,
                                            R.id.container
                                        )
                                    }!!
                                }
                                Status.ERROR -> progress_bar.makeGone()
                                Status.LOADING -> progress_bar.makeVisible()
                            }
                        }
                    })
        }
    }
}