package com.emall.net.fragment.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.changePasswordRequest.ChangePasswordRequest
import com.emall.net.network.model.changePasswordRequest.ChangePasswordRequestParam
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetailData
import com.emall.net.network.model.getProfileRequest.GetProfileRequest
import com.emall.net.network.model.getProfileRequest.GetProfileRequestParam
import com.emall.net.network.model.getProfileResponse.GetProfileResponseData
import com.emall.net.network.model.updateProfileRequest.UpdateProfileRequest
import com.emall.net.network.model.updateProfileRequest.UpdateProfileRequestParam
import com.emall.net.utils.*
import com.emall.net.utils.Constants.CUSTOMER_EMAIL
import com.emall.net.utils.Constants.CUSTOMER_ID
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.snack
import com.emall.net.utils.ViewUtils.checkPasswordLength
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.ByteArrayOutputStream
import android.webkit.MimeTypeMap
import com.emall.net.network.model.updateProfileRequest.ImageData
import java.io.File
import android.graphics.BitmapFactory
import android.util.Base64

import java.io.FileNotFoundException

import java.io.FileInputStream





class ProfileFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var profileData = ArrayList<GetProfileResponseData>()
    private var countryList = ArrayList<GetCountryDetailData>()
    private var countryName = ArrayList<String>()
    private var countryCellCode: String? = ""
    private var imageType: String? = ""
    private var imageName: String? = ""
    private var encodedImage: String? = ""
    private var isAllConditionFulfilled = false
    private var newsLetterSubscription = false
    private var isProfilePicChanged = false
    private lateinit var imageData: ImageData
    private lateinit var imageList :ArrayList<com.emall.net.network.model.updateProfileRequest.File>
    private val TAG = ProfileFragment::class.java.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("staticPages")
                (activity as SellerActivity).setToolbarTitle(getString(R.string.my_profile_details))
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("staticPages")
                (activity as BuyerActivity).setToolbarTitle(getString(R.string.my_profile_details))
            }
        }
        imageList = ArrayList()
        imageList.clear()
        imageData = ImageData(imageList)
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)
        setUp()
    }

    private fun setUp() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        getCountryDetail()

        edit_first_name.setOnClickListener {
            first_name_value.isEnabled = true
            first_name_value.requestFocus()
            first_name_value.setSelection(first_name_value.text.length)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        edit_last_name.setOnClickListener {
            last_name_value.isEnabled = true
            last_name_value.requestFocus()
            last_name_value.setSelection(last_name_value.text.length)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        edit_email.setOnClickListener {
            email_value.isEnabled = true
            email_value.requestFocus()
            email_value.setSelection(email_value.text.length)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        edit_mobile.setOnClickListener {
            mobile_value.isEnabled = true
            mobile_value.requestFocus()
            mobile_value.setSelection(mobile_value.text.length)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        edit_country.setOnClickListener {
            if (countryList.size > 0) {
                val popUp: PopupWindow = popUpCountryWindow()
                popUp.showAtLocation(profile_layout, Gravity.CENTER, 10, 10)
            }
        }
        view2.setOnClickListener { uploadImage() }

        update_profile_button.setOnClickListener {
            (ViewUtils.checkTextViewValidation(
                first_name_value,
                getString(R.string.enter_first_name)
            )
//                    && ViewUtils.checkTextViewValidation(
//                email_value,
//                getString(R.string.enter_email)
//            ) && ViewUtils.isEmailValid(
//                email_value,
//                getString(R.string.enter_email)
//            ) && ViewUtils.checkTextViewValidation(
//                mobile_number_value,
//                getString(R.string.enter_mobile)
//            )
                    ).also { isAllConditionFulfilled = it }
            if (isAllConditionFulfilled) {
                updateProfile()
            }
        }
        news_letter_checkbox.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            newsLetterSubscription = isChecked == true
        })

        edit_new_password.setOnClickListener(View.OnClickListener {
            new_password_value.isEnabled = true
            new_password_value.requestFocus()
            new_password_value.setSelection(new_password_value.text.length)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        })

        edit_confirm_password.setOnClickListener(View.OnClickListener {
            confirm_password_value.isEnabled = true
            confirm_password_value.requestFocus()
            confirm_password_value.setSelection(confirm_password_value.text.length)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        })

        change_password_button.setOnClickListener {
            (ViewUtils.checkTextViewValidation(
                new_password_value,
                getString(R.string.please_enter_new_password)
            ) && ViewUtils.checkTextViewValidation(
                confirm_password_value,
                getString(R.string.please_enter_confirm_password)
            ) && checkPasswordLength(new_password_value,getString(R.string.password_length_error))
                    && ViewUtils.isPasswordMatched(
                new_password_value,
                confirm_password_value,
                getString(R.string.password_does_not_match)
            ) )
                .also { isAllConditionFulfilled = it }
            if (isAllConditionFulfilled) {
                changePassword()
            }
        }
    }

    private fun getCountryDetail() {
        activity?.let { ViewUtils.showProgressBar(it) }
        viewModel.getCountryDetail(Utility.getLanguage())
            .observe(viewLifecycleOwner, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                countryList.clear()
                                countryName.clear()
                                countryList = it.data
                                for (country in it.data) {
                                    countryName.add(country.name + " (+${country.cel_code})")
                                }
                                getProfileData()
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            it.message?.let { it1 -> Log.e("error", it1) }
                        }
                        Status.LOADING -> {
                        }
                    }
                }
            })
    }

    private fun getProfileData() {
        val getProfileRequest = GetProfileRequest(
            param = GetProfileRequestParam(
                PreferenceHelper.readString(CUSTOMER_EMAIL)!!
            )
        )
        viewModel.getProfile(getProfileRequest,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                profileData.clear()
                                profileData = it.data
                                setProfileData()
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun setProfileData() {
        if (profileData.isNotEmpty()) {
            user_name.text =
                profileData[0].fname + " " + if (profileData[0].lname.isNullOrEmpty()) {
                    ""
                } else {
                    profileData[0].lname
                }
            user_email.text = profileData[0].email
            if (!profileData[0].fname.isNullOrEmpty()) {
                first_name_value.setText(profileData[0].fname)
                if (first_name_value.length() == 0) {
                    first_name_value.hint = getString(R.string.first_name)
                }
            }
            if (!profileData[0].lname.isNullOrEmpty()) {
                last_name_value.setText(profileData[0].lname)
            }
            if (!profileData[0].email.isNullOrEmpty()) {
                email_value.setText(profileData[0].email)
            }
            if (!profileData[0].mob.isNullOrEmpty()) {
                mobile_value.setText(profileData[0].mob)
            }
            if(!profileData[0].image_data.isNullOrEmpty()){
                Picasso.get().load(profileData[0].image_data).into(profile_image)
            }

            if (profileData[0].newsletter) {
                news_letter_checkbox.isChecked = true
                newsLetterSubscription = true
            } else {
                news_letter_checkbox.isChecked = false
                newsLetterSubscription = false
            }
            setCountryName(profileData[0].country_code)
        }
    }

    private fun setCountryName(countryCode: String) {
        if (countryList.isNotEmpty()) {
            for (item in countryList) {
                if (countryCode.equals("+${item.cel_code}", true)) {
                    country_value.setText(item.name + " (+${item.cel_code})")
                    countryCellCode = item.cel_code.toString()
                }
            }
        }
        if (!profile_layout.isVisible) {
            profile_layout.makeVisible()
        }
    }

    private fun popUpCountryWindow(): PopupWindow {
        val popupWindow = PopupWindow(context)
        val adapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(),
                R.layout.checkedradiotextviewenglish,
                countryName)
        val listViewSort = ListView(context)
        listViewSort.adapter = adapter
        listViewSort.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                country_value.setText(countryName[position])
                countryCellCode = countryList[position].cel_code.toString()
                popupWindow.dismiss()
            }
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(profile_layout, Gravity.CENTER, 10, 200)
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.solid_white_round_background))
        popupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
        popupWindow.width = displayMetrics.widthPixels
        popupWindow.contentView = listViewSort
        return popupWindow
    }

    private fun updateProfile() {
        val updateProfileRequest = UpdateProfileRequest(
            param = UpdateProfileRequestParam(
                PreferenceHelper.readInt(CUSTOMER_ID)!!,
                email_value.text.toString(),
                first_name_value.text.toString(),imageData,isProfilePicChanged,
                last_name_value.text.toString(),
                "+" + countryCellCode + mobile_value.text.toString(),
                newsLetterSubscription,"+" + countryCellCode + mobile_value.text.toString()
            )
        )
        viewModel.updateProfile(updateProfileRequest,
            Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                Toast.makeText(requireContext(),
                                    R.string.profile_updated_successfully,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun changePassword() {
        val changePasswordRequest = ChangePasswordRequest(
            param = ChangePasswordRequestParam(
                PreferenceHelper.readInt(CUSTOMER_ID)!!,
                new_password_value.text.toString(),
            )
        )
        viewModel.changePassword(changePasswordRequest)
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                Toast.makeText(requireContext(),
                                    it.message,
                                    Toast.LENGTH_SHORT).show()
                                openHomePage()
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun openHomePage() {
        val language = PreferenceHelper.readString(Constants.APP_LOCALE)
        val selectedCountry = PreferenceHelper.readString(Constants.SELECTED_COUNTRY)
        val selectedCountryName = PreferenceHelper.readString(Constants.SELECTED_COUNTRY_NAME)
        val selectedCurrency = PreferenceHelper.readString(Constants.SELECTED_CURRENCY)
        val currencyRate = PreferenceHelper.readFloat(Constants.CURRENCY_RATE)
        PreferenceHelper.clear()
        PreferenceHelper.writeString(Constants.APP_LOCALE, language!!)
        PreferenceHelper.writeString(Constants.SELECTED_COUNTRY, selectedCountry!!)
        PreferenceHelper.writeString(Constants.SELECTED_COUNTRY_NAME, selectedCountryName!!)
        PreferenceHelper.writeString(Constants.SELECTED_CURRENCY, selectedCurrency!!)
        PreferenceHelper.writeFloat(Constants.CURRENCY_RATE, currencyRate!!)
        PreferenceHelper.writeBoolean(Constants.IS_NEW_DEVICE, true)
        PreferenceHelper.writeInt(CUSTOMER_ID, 0)
        recreateActivity()
    }

    private fun recreateActivity() {
        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).finish()
                (activity as SellerActivity).overridePendingTransition(0, 0)
                startActivity((activity as SellerActivity).intent)
                (activity as SellerActivity).overridePendingTransition(0, 0)
                (activity as SellerActivity).finish()
            }
            else -> {
                (activity as BuyerActivity).finish()
                (activity as BuyerActivity).overridePendingTransition(0, 0)
                startActivity((activity as BuyerActivity).intent)
                (activity as BuyerActivity).overridePendingTransition(0, 0)
                (activity as BuyerActivity).finish()
            }
        }
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
            isProfilePicChanged = true
            imageType = getMimeType(path)
            imageName = getImageName(path)
            encodedImage = encodeImage(path!!)
            imageList.add(com.emall.net.network.model.updateProfileRequest.File(encodedImage!!,
                imageName!!,imageType!!))
            imageData = ImageData(imageList)
            showImages(it)
        }
    }

    private fun getImageName(path: String?): String {
        val file = File(path!!)
        return file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase()
    }

    private var launchSomeActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    val bitmap = result.data?.extras?.get("data") as Bitmap

                    Log.d(TAG, ":dfsdf $bitmap")
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes)
                    val path = MediaStore.Images.Media.insertImage(context?.contentResolver,bitmap,"myphotos",null)
                    imageType = getMimeType(path)
                    isProfilePicChanged = true
                    imageName = getImageName(path)
                    encodedImage = encodeImage(path)
                    imageList.add(com.emall.net.network.model.updateProfileRequest.File(encodedImage!!,
                        imageName!!,imageType!!))
                    imageData = ImageData(imageList)
                    showImages(Uri.parse(path))
                }
            }
        }

    private fun showImages(parse: Uri?) = Picasso.get().load(parse).into(profile_image)

    private var requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if(isGranted){
            val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            launchSomeActivity.launch(takePicture)
        }else{
            profile_layout.snack(getString(R.string.permission_denied))
        }
    }

    private fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    private fun encodeImage(path: String): String? {
        val imageFile = File(path)
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(imageFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val bm = BitmapFactory.decodeStream(fis)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        //Base64.de
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
}