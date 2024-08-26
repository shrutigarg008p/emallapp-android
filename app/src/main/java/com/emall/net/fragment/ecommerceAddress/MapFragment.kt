package com.emall.net.fragment.ecommerceAddress

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.adapter.PlacesAutoCompleteAdapter
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.utils.Constants.ADDRESS_TYPE
import com.emall.net.utils.PreferenceHelper
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.popBackStack
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_map.*
import java.io.IOException
import java.util.*


@Suppress("DEPRECATION")
class MapFragment : Fragment(), OnMapReadyCallback, PlacesAutoCompleteAdapter.ClickListener {
    private lateinit var geocoder: Geocoder
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var MY_PERMISSIONS_REQUEST_LOCATION: Int = 100
    private val permissionsRequired = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    private var permissionGranted = false
    private var somePermissionsForeverDenied = false
    private lateinit var locationRequest: LocationRequest
    private var REQUEST_CHECK_SETTINGS = 99
    private lateinit var googleMap: GoogleMap
    private lateinit var placesAutoCompleteAdapter: PlacesAutoCompleteAdapter
    private lateinit var viewModel: MainViewModel
    private var countryCode = ArrayList<String>()
    private var AddressId: String = ""
    private var FullName: String = ""
    private var City: String = ""
    private var ZipCode: String = ""
    private var Address: String = ""
    private var CountryCode: String = ""
    private var PhoneNumber: String = ""
    private var Latitude: String = ""
    private var Longitude: String = ""
    private var Type: String = ""
    private var enterManually: Boolean = false
    private var autoCompletePlaces: Boolean = false
    private var mIsCameraIdle = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (activity) {
            is SellerActivity -> (activity as SellerActivity).showHideToolbar("hide")
            else -> (activity as BuyerActivity).showHideToolbar("hide")
        }
        Type = PreferenceHelper.readString(ADDRESS_TYPE)!!
        if (Type.equals("edit", true)) {
            setArgumentValues()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        if (mapFragment != null) {
            mapFragment.getMapAsync(this)
            val locationButton =
                (mapFragment.view?.findViewById<View>("1".toInt())?.parent as View).findViewById<View>(
                    "2".toInt())
            val rlp = locationButton.layoutParams as RelativeLayout.LayoutParams
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            rlp.setMargins(0, 0, 30, 30)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Create a new PlacesClient instance
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), getString(R.string.google_maps_key))
        }
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

    private fun setArgumentValues() {
        if (arguments != null) {
            AddressId = arguments?.getString("addressId")!!
            FullName = arguments?.getString("fullName")!!
            PhoneNumber = arguments?.getString("phoneNumber")!!
            City = arguments?.getString("city")!!
            CountryCode = arguments?.getString("countryCode")!!
            Address = arguments?.getString("address")!!
            ZipCode = arguments?.getString("zipCode")!!
            Latitude = arguments?.getString("latitude")!!
            Longitude = arguments?.getString("longitude")!!
        }
    }

    private fun setUp() {
        Places.createClient(requireContext())
        placesAutoCompleteAdapter = PlacesAutoCompleteAdapter(requireContext())
        val linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        auto_complete_recycler_view.layoutManager = linearLayoutManager
        placesAutoCompleteAdapter?.setClickListener(this)
        auto_complete_recycler_view.adapter = placesAutoCompleteAdapter
        placesAutoCompleteAdapter?.notifyDataSetChanged()
        auto_complete_recycler_view.isNestedScrollingEnabled = false

        place_search.addTextChangedListener(filterTextWatcher)

        map_cancel_icon.setOnClickListener(View.OnClickListener {
            place_search.setText("")
        })

        map_back_icon.setOnClickListener(View.OnClickListener {
            redirectToPreviousFragment()
        })

        manually_enter_address_btn.setOnClickListener(View.OnClickListener {
            enterManually = true
            openAddressDetailFragment()
        })
    }

    private fun openAddressDetailFragment() {
        val bundle = Bundle()
        if (enterManually) {
            bundle.putString("type", "")
            bundle.putString("addressId", "")
            bundle.putString("fullName", "")
            bundle.putString("phoneNumber", "")
            bundle.putString("city", "")
            bundle.putString("countryCode", "")
            bundle.putString("address", "")
            bundle.putString("zipCode", "")
            bundle.putString("latitude", "")
            bundle.putString("longitude", "")
        } else {
            bundle.putString("type", Type)
            bundle.putString("addressId", AddressId)
            bundle.putString("fullName", FullName)
            bundle.putString("phoneNumber", PhoneNumber)
            bundle.putString("city", City)
            bundle.putString("countryCode", CountryCode)
            bundle.putString("address", Address)
            bundle.putString("zipCode", ZipCode)
            bundle.putString("latitude", Latitude)
            bundle.putString("longitude", Longitude)
        }
        val transaction = this.parentFragmentManager.beginTransaction()
        val fragment = AddressDetailFragment()
        fragment.arguments = bundle
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
    }

    private fun getCountryDetail() {
        activity?.let { ViewUtils.showProgressBar(it) }
        viewModel.getCountryDetail(Utility.getLanguage())
            .observe(viewLifecycleOwner, { it ->
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.let {
                                countryCode.clear()
                                for (code in it.data) {
                                    countryCode.add(code.code)
                                }
                                getLastLocation()
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

    private val filterTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if ("$s" != "") {
                placesAutoCompleteAdapter?.filter?.filter(s.toString())
                auto_complete_recycler_view.makeVisible()
            } else {
                auto_complete_recycler_view.makeGone()
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    private fun redirectToPreviousFragment() {
        if (activity is SellerActivity) {
            (activity as SellerActivity).popBackStack()
        } else {
            (activity as BuyerActivity).popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.e("onStart", "onStart")
        if (checkPlayServices()) {
            if (!checkPermissions()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermission()
                }
            } else {
                checkGPSEnable()
            }
        } else {
            Toast.makeText(requireContext(),
                R.string.google_play_services_not_available,
                Toast.LENGTH_SHORT).show()
        }
    }

    //    function to check google play services
    private fun checkPlayServices(): Boolean {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val result = googleApiAvailability.isGooglePlayServicesAvailable(requireActivity())
        return when {
            result != ConnectionResult.SUCCESS -> {
                when {
                    googleApiAvailability.isUserResolvableError(result) -> {
                        googleApiAvailability.getErrorDialog(requireActivity(), result, 1).show()
                    }
                }
                false
            }
            else -> true
        }
    }

    private fun checkPermissions(): Boolean {
        return checkSelfPermission(requireContext(),
            permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
            requireContext(),
            permissionsRequired[1]) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        requestPermissions(permissionsRequired, 100)
    }

    private fun checkGPSEnable() {
        locationRequest = LocationRequest.create()
        locationRequest?.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = 50F
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(context)
                .checkLocationSettings(builder.build())

        result.addOnCompleteListener {
            try {
                val response: LocationSettingsResponse = it.getResult(ApiException::class.java)
                Toast.makeText(context, "GPS is On", Toast.LENGTH_SHORT).show()
                if (countryCode.size == 0) {
                    getCountryDetail()
                } else {
                    getLastLocation()
                }
            } catch (e: ApiException) {

                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        val resolvableApiException = e as ResolvableApiException
                        resolvableApiException.startResolutionForResult(
                            requireActivity(),
                            REQUEST_CHECK_SETTINGS
                        )
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        // USER DEVICE DOES NOT HAVE LOCATION OPTION
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        Toast.makeText(requireContext(), "GPS enabled", Toast.LENGTH_SHORT).show()
                        if (countryCode.size == 0) {
                            getCountryDetail()
                        } else {
                            getLastLocation()
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                        Toast.makeText(requireContext(), "GPS not enabled", Toast.LENGTH_SHORT)
                            .show()
                        checkGPSEnable()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (permissions.isEmpty()) {
                    return
                }
                var allPermissionGranted = true
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    for (result in grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            allPermissionGranted = false
                            break
                        }
                    }
                }
                if (!allPermissionGranted) {
                    for (perm in permissions) {
                        if (shouldShowRequestPermissionRationale(perm)
                        ) {
                            permissionGranted = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermission()
                            }
                        } else {
                            if (checkPermissions()) {
                                permissionGranted = true
                                checkGPSEnable()
                            } else {
                                somePermissionsForeverDenied = true
                            }
                        }
                    }
                    if (somePermissionsForeverDenied) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Permissions Required")
                            .setMessage("You have forcefully denied some of the required permissions " +
                                    "for this action. Please open settings, go to permissions and allow them.")
                            .setPositiveButton("Go to Settings") { dialog, which ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package",
                                        requireContext().packageName,
                                        null))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                dialog.dismiss()
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                } else {
                    permissionGranted = true
                    checkGPSEnable()
                }
                return
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (Type.equals("edit", true)) {
            if (Latitude == "" || Longitude == "") {
                activity?.let { ViewUtils.hideProgressBar() }
                enterManually = false
                openAddressDetailFragment()
                PreferenceHelper.writeString(ADDRESS_TYPE, "")
            } else {
                val currentLatLong = LatLng(Latitude.toDouble(), Longitude.toDouble())
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 18.0f))
                getGeoCoderLocationDetails(currentLatLong)
            }
        } else {
            if (Latitude == "" || Longitude == "") {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                    mLocationCallback,
                    null)
                googleMap.isMyLocationEnabled = true
            } else {
                val currentLatLong = LatLng(Latitude.toDouble(), Longitude.toDouble())
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 18.0f))
                getGeoCoderLocationDetails(currentLatLong)
            }
        }
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                val location = locationList[locationList.size - 1]
                val currentLatLong = LatLng(location.latitude, location.longitude)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 18.0f))
                getGeoCoderLocationDetails(currentLatLong)
            }
        }
    }

    private fun getGeoCoderLocationDetails(GeoCoder: LatLng) {
        activity?.let { ViewUtils.hideProgressBar() }
        geocoder = Geocoder(context, Locale.getDefault())
        Latitude = GeoCoder.latitude.toString()
        Longitude = GeoCoder.longitude.toString()
        try {
            val addressList: List<Address> = geocoder.getFromLocation(
                GeoCoder.latitude, GeoCoder.longitude, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                var address = addressList[0]
                val sb = StringBuilder()
                for (i in 0 until address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)).append("\n")
                }
                place_search.setText(address.getAddressLine(0))
                auto_complete_recycler_view.makeGone()
                if (countryCode.size != 0) {
                    if (countryCode.contains(address.countryCode)) {
                        Log.e("address", address.toString())
                        countryInList(address)
                    } else {
                        countryNotInList()
                    }
                }
            }
        } catch (e: IOException) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun countryInList(address: Address) {
        not_deliverable_container.makeGone()
        confirm_location_button.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_orange_gradient))
        City = if (address.locality != null) {
            address.locality
        } else {
            if (!autoCompletePlaces) {
                City
            } else {
                ""
            }

        }
        CountryCode = address.countryCode!!
        Address = address.getAddressLine(0)
        ZipCode = if (address.postalCode != null) {
            address.postalCode
        } else {
            if (!autoCompletePlaces) {
                ZipCode
            } else {
                ""
            }
        }
        confirm_location_button.setOnClickListener(View.OnClickListener {
            enterManually = false
            openAddressDetailFragment()
        })
    }

    private fun countryNotInList() {
        confirm_location_button.setOnClickListener(View.OnClickListener {
            enterManually = false
        })
        not_deliverable_container.makeVisible()
        confirm_location_button.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_gray_gradient))
    }

    //    on selecting auto complete places item
    override fun click(place: Place?) {
        autoCompletePlaces = true
        try {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
        } catch (e: Exception) {
        }
        auto_complete_recycler_view.makeGone()
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place!!.latLng, 18.0f))
        val currentLatLong = LatLng(place.latLng!!.latitude, place.latLng!!.longitude)
        getGeoCoderLocationDetails(currentLatLong)
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = false
        googleMap.uiSettings.isZoomGesturesEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        /*set default Saudi Location*/
        val currentLatLong = LatLng(24.774265,46.738586)
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,
            18.0f))
        getGeoCoderLocationDetails(currentLatLong)

//        marker get location on tap
        googleMap.setOnMapClickListener { latLng ->
            if (googleMap != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f))
                getGeoCoderLocationDetails(latLng)
            }
        }

        // marker get location on move
        googleMap.setOnCameraIdleListener(GoogleMap.OnCameraIdleListener {
            if (mIsCameraIdle) {
                mIsCameraIdle = false;
                if (googleMap != null) {
                    val currentLatLong = googleMap.cameraPosition.target
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,
                        18.0f))
                    getGeoCoderLocationDetails(currentLatLong)
                }
            } else {
                mIsCameraIdle = true;
            }
        })
    }

}