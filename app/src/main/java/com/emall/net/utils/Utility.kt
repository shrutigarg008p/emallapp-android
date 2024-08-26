package com.emall.net.utils

import android.content.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Context.INPUT_METHOD_SERVICE
import android.database.Cursor
import android.net.*
import android.os.*
import android.provider.*
import android.text.*
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.*
import androidx.appcompat.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.lifecycle.Observer
import com.emall.net.R
import com.emall.net.customview.*
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.utils.Constants.CURRENCY_RATE
import com.emall.net.utils.Constants.SELECTED_CURRENCY
import com.emall.net.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.*
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.stc_tap_fragment.*
import java.util.*
import kotlin.math.roundToInt


object Utility {


    fun View.hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    fun View.makeGone() {
        this.visibility = View.GONE
    }

    fun View.makeVisible() {
        this.visibility = View.VISIBLE
    }

    fun View.makeInvisible() {
        this.visibility = View.INVISIBLE
    }

    fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
        val intent = Intent(this, it)
        intent.putExtras(Bundle().apply(extras))
        startActivity(intent)
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().addToBackStack(javaClass.simpleName).commit()
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction {
            add(frameId,
                fragment,
                fragment.javaClass.simpleName)
        }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
        supportFragmentManager.inTransaction {
            replace(
                frameId,
                fragment,
                fragment.javaClass.simpleName
            )
        }
    }

    fun AppCompatActivity.popBackStack() {
        supportFragmentManager.popBackStack()
    }

    fun AppCompatActivity.popBackStackInclusive() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack(
                supportFragmentManager.getBackStackEntryAt(0).id,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
    }

    fun AppCompatActivity.getCurrentFragment(): Fragment? {
        val fragmentManager = supportFragmentManager
        var fragmentTag: String? = ""

        if (fragmentManager.backStackEntryCount > 0)
            fragmentTag =
                fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 1).name

        return fragmentManager.findFragmentByTag(fragmentTag)
    }

    fun View.snack(message: String, length: Int = Snackbar.LENGTH_LONG) {
        val snack = Snackbar.make(this, message, length)
        snack.show()
    }

    fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
        setAction(actionRes, listener)
        color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
    }

    fun AppCompatActivity.checkPerm(permission: String) = ActivityCompat.checkSelfPermission(
        this,
        permission
    )

    fun AppCompatActivity.shouldRequestPermissionRationale(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

    fun AppCompatActivity.requestAllPermissions(permissionsArray: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
    }

    fun getPath(context: Context, uri: Uri): String? {
        val isKitkatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        when {
            isKitkatOrAbove && DocumentsContract.isDocumentUri(context, uri) -> {
                // ExternalStorageProvider
                when {
                    isExternalStorageDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":".toRegex()).toTypedArray()
                        val type = split[0]
                        when {
                            "primary".equals(type,
                                ignoreCase = true) -> return Environment.getExternalStorageDirectory()
                                .toString() + "/" + split[1]
                        }
                    }
                    isDownloadsDocument(uri) -> {
                        val id = DocumentsContract.getDocumentId(uri)
                        val contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"),
                            java.lang.Long.valueOf(
                                id
                            )
                        )
                        return getDataColumn(context, contentUri, null, null)
                    }
                    isMediaDocument(uri) -> {
                        val docId = DocumentsContract.getDocumentId(uri)
                        val split = docId.split(":".toRegex()).toTypedArray()
                        val type = split[0]
                        var contentUri: Uri? = null
                        when (type) {
                            "image" -> contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                            "video" -> contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            "audio" -> contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                        }
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(split[1])
                        return getDataColumn(context, contentUri, selection, selectionArgs)
                    }
                }
            }
            "content".equals(uri.scheme, ignoreCase = true) -> return getDataColumn(
                context,
                uri,
                null,
                null
            )
            "file".equals(uri.scheme, ignoreCase = true) -> return uri.path
        }
        return null
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?,
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!,
                projection,
                selection,
                selectionArgs,
                null
            )
            when {
                cursor != null && cursor.moveToFirst() -> {
                    val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                    return cursor.getString(columnIndex)
                }
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean =
        "com.android.externalstorage.documents" == uri.authority

    private fun isDownloadsDocument(uri: Uri): Boolean =
        "com.android.providers.downloads.documents" == uri.authority

    private fun isMediaDocument(uri: Uri): Boolean =
        "com.android.providers.media.documents" == uri.authority

    fun EditText.clear() = this.editableText.clear()

    fun hasInternet(context: Context): Boolean {
        var isConnected = false
        val connectivityManager =
            context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val activeNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            isConnected = when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                activeNetworkInfo?.run {
                    isConnected = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return isConnected
    }

    fun AppCompatActivity.getQuoteIdFunc(
        viewModel: MainViewModel,
        activity: AppCompatActivity,
    ): Int {
        var id: Int = 0
        val quoteId = QuoteIdParams(123)
        val token = PreferenceHelper.readString(Constants.CUSTOMER_TOKEN)
        viewModel.getQuoteId("Bearer $token", quoteId).observe(activity, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data.let { it ->
                            id = it.toString().toInt()
                        }
                    }
                    Status.ERROR -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                    Status.LOADING -> {
                        Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                    }
                }
            }
        })
        return id
    }

    fun getLanguage(): String {
        return if (!Locale.getDefault().language.equals(Locale.ENGLISH.language, false)) {
            "ar"
        } else
            "en"
    }

    fun View.showPopUp(subHeading: String): Boolean{
        val builder = AlertDialog.Builder(this.context)
        val dialogView = LayoutInflater.from(this.context)
            .inflate(R.layout.common_pop_up, this.findViewById(R.id.content), false)
        builder.setView(dialogView)

        val message: TextView = dialogView.findViewById(R.id.sub_heading)
        message.text = subHeading
        val okBtn : TextView= dialogView.findViewById(R.id.ok_btn)
        val alertDialog = builder.create()
        alertDialog.show()
        okBtn.setOnClickListener {
            alertDialog.dismiss()
            return@setOnClickListener
        }
        return false
    }

    fun isCustomerLoggedIn(): Boolean {
        return PreferenceHelper.readInt(Constants.CUSTOMER_ID) != 0
    }

    fun changedCurrencyPrice(price: Double): String =
        "${PreferenceHelper.readString(SELECTED_CURRENCY)} ${
            (price * PreferenceHelper.readFloat(CURRENCY_RATE)!!).roundToInt()
        }"

     fun getDeviceType(): String = when {
        PreferenceHelper.readBoolean(Constants.IS_NEW_DEVICE)!! -> "new"
        else -> "used"
    }

     fun getStoreCode(): Int = when {
        Locale.getDefault().language.equals(Constants.ENGLISH_LOCALE) -> 1
        else -> 2
    }
     fun View.setTopMargin(margin: Int) {
        (layoutParams as ViewGroup.MarginLayoutParams).topMargin = margin
    }

     fun View.setBottomMargin(margin: Int) {
        (layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = margin
    }

    fun checkEmail(email: String): Boolean = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }
}