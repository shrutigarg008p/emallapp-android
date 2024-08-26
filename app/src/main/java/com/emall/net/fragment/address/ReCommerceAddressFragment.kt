package com.emall.net.fragment.address

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.emall.net.R
import com.emall.net.activity.dashboard.*
import com.emall.net.network.api.*
import com.emall.net.network.model.addSellerAddress.AddressParams
import com.emall.net.network.model.evaluationUserAddress.AddressRequestBodyParams
import com.emall.net.network.model.filter.*
import com.emall.net.utils.*
import com.emall.net.utils.Constants.EVALUATOR_USER_ADDRESS
import com.emall.net.utils.Constants.LOGIN_TYPE
import com.emall.net.utils.Constants.SELLER
import com.emall.net.utils.Constants.USER_EMAIL
import com.emall.net.utils.Constants.USER_PASSWORD
import com.emall.net.utils.Utility.afterTextChanged
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.popBackStack
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import com.google.android.gms.common.*
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.model.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_manually_address_fragment.*
import kotlinx.android.synthetic.main.fragment_manually_address_fragment.scrollView
import java.io.IOException


class ReCommerceAddressFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private var countryName: String? = ""
    private var countryCode: String? = ""
    private var stateCode: String? = ""
    private var phoneCode: String? = "973"
    private var countries = ArrayList<String>()
    private var countriesList = ArrayList<CountriesData>()
    private var states = ArrayList<String>()
    private var stateList = ArrayList<StatesData>()
    private var phoneCodeList = ArrayList<String>()

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdates = false
    private var addressId: Int = 0
    private var addressToUpdate: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manually_address_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (activity) {
            is SellerActivity -> {
                (activity as SellerActivity).showHideToolbar("")
                (activity as SellerActivity).setToolbarTitle("Address")
            }
            else -> {
                (activity as BuyerActivity).showHideToolbar("")
                (activity as BuyerActivity).setToolbarTitle("Address")
            }
        }

        if (arguments != null) {
            addressId = requireArguments().getInt("addressId")
            addressToUpdate = requireArguments().getBoolean("update")
        }

        token = PreferenceHelper.readString(Constants.SELLER_EVALUATOR_TOKEN)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*
         * check GPS and Mobile Network permissions and are enabled or not
         */
        openLocationDialog()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    // Update UI with location data
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    lastLocation = location
                    placeMarkerOnMap(currentLatLong)
                    Log.d(
                        "TAG",
                        "onLocationResult: " + location.latitude.toString() + ", " + location.longitude
                    )
                }
            }
        }

        full_name.afterTextChanged {
            if(it.isEmpty()) full_name.error = "Please enter the full name"
            full_name.requestFocus()
        }

        phone_number.afterTextChanged {
            if (phoneCode.equals(
                    "91",
                    true
                ) && (phone_number.length() > 10 || phone_number.length() < 10)
            ) {
                phone_number.error = "Please enter the valid mobile number"
                phone_number.requestFocus()
            } else if (!phoneCode.equals(
                    "91",
                    true
                ) && (phone_number.length() > 9 || phone_number.length() < 9)
            ) {
                phone_number.error = "Please enter the valid mobile number"
                phone_number.requestFocus()
            }
        }

        city.afterTextChanged {
            if(it.isEmpty()) city.error = "Please enter the city"
        }

        zip.afterTextChanged {
            if(it.isEmpty()) zip.error = "Please enter the zip"
            if(it.length < 5) zip.error = "Please enter the correct zip"
        }

        createLocationRequest()

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().apiClient().create(ApiService::class.java)
            )
        )
            .get(MainViewModel::class.java)

        viewModel.getCountries(
            "Bearer $token",
            PreferenceHelper.readString(USER_EMAIL)!!,
            PreferenceHelper.readString(USER_PASSWORD)!!,
            Utility.getLanguage()
        ).observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        resource.data?.DATA.let { it ->
                            progress_bar.makeGone()
                            countries.clear()
                            for (country in it!!) {
                                countries.add(country.name)
                            }
                            country_spinner.setItems(countries)
                            countriesList = it
                            phoneCodeList.clear()
                            for (mobile in it) {
                                phoneCodeList.add(mobile.phone_code)
                            }
                            country_code.setItems(phoneCodeList)
                        }
                    }
                    Status.ERROR -> {
                        progress_bar.makeGone()
                    }
                    Status.LOADING -> {
                        progress_bar.makeVisible()
                    }
                }
            }
        })

        country_spinner.setOnItemSelectedListener { view, position, id, item ->
            countryName = countriesList[position].name
//
            countryCode = countriesList[position].iso2

            viewModel.getStatesByCountry(
                "Bearer $token", countryCode!!,
                PreferenceHelper.readString(USER_EMAIL)!!,
                PreferenceHelper.readString(USER_PASSWORD)!!,
                Utility.getLanguage()
            ).observe(viewLifecycleOwner, {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA.let { it ->
                                states.clear()
                                for (state in it!!) {
                                    states.add(state.name)
                                }
                                state_spinner.setItems(states)
                                stateList = it
                                stateCode = stateList[position - 1].state_code
                            }
                        }
                        Status.ERROR -> {

                        }
                        Status.LOADING -> {

                        }
                    }
                }
            })
        }

        country_code.setOnItemSelectedListener { view, position, id, item ->
            phoneCode = phoneCodeList[position]
        }

        state_spinner.setOnItemSelectedListener { view, position, id, item ->
            stateCode = stateList[position].state_code
            Log.d("TAG", "onViewCreated: " + stateCode)
        }
        submit_btn.setOnClickListener {
            if (full_name.text.toString().isEmpty() && address_1.text.toString().isEmpty() &&
                address_2.text.toString()
                    .isEmpty() && countryCode.isNullOrEmpty() && stateCode.isNullOrEmpty()
                && phone_number.text.toString().isEmpty() && zip.text.toString()
                    .isEmpty() && phoneCode.isNullOrEmpty()
            ) {
                scrollView.snack("Please enter valid details to proceed")
            } else {
                when {
                    addressToUpdate -> when {
                        PreferenceHelper.readString(LOGIN_TYPE)!!.contentEquals(SELLER) ->
                            viewModel.updateAddressForSeller(
                                addressId, "Bearer $token",
                                AddressRequestBodyParams(
                                    full_name.text.toString(),
                                    address_1.text.toString(),
                                    address_2.text.toString(),
                                    city.text.toString(),
                                    countryCode!!,
                                    stateCode!!,
                                    zip.text.toString(),
                                    "",
                                    "",
                                    phone_number.text.toString(),
                                    phoneCode!!
                                ), Utility.getLanguage()
                            ).observe(viewLifecycleOwner, {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.SUCCESS -> resource.data?.let { it ->
                                            progress_bar.makeGone()
                                            scrollView.snack(it.MESSAGE)
                                        }
                                        Status.LOADING -> progress_bar.makeVisible()
                                        Status.ERROR -> progress_bar.makeGone()
                                    }
                                }
                            })
                        else -> viewModel.updateAddressForEvaluationUser(
                            addressId, "Bearer $token",
                            AddressRequestBodyParams(
                                full_name.text.toString(),
                                address_1.text.toString(),
                                address_2.text.toString(),
                                city.text.toString(),
                                countryCode!!,
                                stateCode!!,
                                zip.text.toString(),
                                "",
                                "",
                                phone_number.text.toString(),
                                phoneCode!!
                            ), Utility.getLanguage()
                        ).observe(viewLifecycleOwner, {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> resource.data?.let { it ->
                                        progress_bar.makeGone()
                                        scrollView.snack(it.MESSAGE)
                                    }
                                    Status.LOADING -> progress_bar.makeVisible()
                                    Status.ERROR -> progress_bar.makeGone()
                                }
                            }
                        })
                    }
                    else -> when {
                        PreferenceHelper.readString(LOGIN_TYPE)!!
                            .contentEquals(SELLER) -> viewModel.addSellerAddress(
                            "Bearer $token", AddressParams(
                                address_1.text.toString(),
                                address_2.text.toString(),
                                city.text.toString(),
                                stateCode!!,
                                countryCode!!,
                                zip.text.toString(),
                                "",
                                "",
                                phone_number.text.toString(),
                                phoneCode!!
                            ), Utility.getLanguage()
                        ).observe(viewLifecycleOwner, {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> resource.data?.let { (activity as SellerActivity).popBackStack() }
                                    Status.LOADING -> {
                                    }
                                    Status.ERROR -> {
                                    }
                                }
                            }
                        })
                        else -> {
                            viewModel.addAddressForEvaluationUser(
                                "Bearer $token", AddressRequestBodyParams(
                                    full_name.text.toString(),
                                    address_1.text.toString(),
                                    address_2.text.toString(),
                                    city.text.toString(),
                                    countryCode!!,
                                    stateCode!!,
                                    zip.text.toString(),
                                    "",
                                    "",
                                    phone_number.text.toString(),
                                    phoneCode!!
                                ), Utility.getLanguage()
                            ).observe(viewLifecycleOwner, {
                                it?.let { resource ->
                                    when (resource.status) {
                                        Status.SUCCESS -> {
                                            resource.data?.let { it ->
                                                PreferenceHelper.writeBoolean(
                                                    EVALUATOR_USER_ADDRESS,
                                                    true
                                                )
                                                (activity as BuyerActivity).popBackStack()
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
                }
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(requireActivity())
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdates = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    /*e.startResolutionForResult(this@MapsActivity,
                        REQUEST_CHECK_SETTINGS)*/
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when {
            !locationUpdates -> startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        googleMap.isMyLocationEnabled = false
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.setOnMapClickListener(this)
        getUserCurrentLocation()
    }

    private fun getUserCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {
                val currentLatLong = LatLng(it.latitude, it.longitude)

                val positionBuilder = CameraPosition.Builder()
                positionBuilder.target(currentLatLong)
                positionBuilder.zoom(12.5f)

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()),
                    object : CancelableCallback {
                        override fun onFinish() {
                            placeMarkerOnMap(currentLatLong)
                        }

                        override fun onCancel() {}
                    })
            }
        }
    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        Log.d("TAG", "placeMarkerOnMap: " + currentLatLong.longitude)
        markerOptions.icon(
            bitmapDescriptorFromVector(
                requireActivity(),
                R.drawable.ic_user_location
            )
        )
        googleMap.addMarker(markerOptions)
        getAddress(currentLatLong)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationUpdates = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
            /*Looper.getMainLooper()*/
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    /**
     * check GPS Services is on / off
     */
    private fun openLocationDialog() {
        if (checkPlayServices()) {
            // If this check succeeds, proceed with normal processing.
            // Otherwise, prompt user to get valid Play Services APK.
            val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
            var gpsEnabled = false
            var networkEnabled = false
            try {
                gpsEnabled = lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            try {
                networkEnabled = lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            if (!gpsEnabled || !networkEnabled) {
                // notify user
                MaterialAlertDialogBuilder(requireActivity())
                    .setMessage("Location not enabled")
                    .setPositiveButton(
                        "Open Location Settings"
                    ) { dialog, param -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                    .show()
            }
        } else {
            println("location not supported on device")
            //location not supported on device
        }
    }

    /**
     * check google play services
     */
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

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorDrawableResourceId: Int,
    ): BitmapDescriptor? {
        val background = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        background?.setBounds(0, 0, background.intrinsicWidth, background.intrinsicHeight)
        val vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId)
        vectorDrawable?.setBounds(
            40,
            20,
            vectorDrawable.intrinsicWidth + 40,
            vectorDrawable.intrinsicHeight + 20
        )
        var bitmap: Bitmap? = null
        if (background != null) {
            bitmap = Bitmap.createBitmap(
                background.intrinsicWidth,
                background.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        var canvas: Canvas? = null
        if (bitmap != null) {
            canvas = Canvas(bitmap)
        }
        if (background != null) {
            if (canvas != null) {
                background.draw(canvas)
            }
        }
        if (vectorDrawable != null) {
            if (canvas != null) {
                vectorDrawable.draw(canvas)
            }
        }
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getAddress(latLng: LatLng): String {
        // 1
        val geoCoder = Geocoder(requireActivity())
        val addresses: List<Address>?
        val address: Address?
        var addressText = ""

        try {
            // 2
            addresses = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            // 3
            if (null != addresses && addresses.isNotEmpty()) {
                address = addresses[0]
                Log.d("TAG", "getAddress: " + addresses[0])
                for (i in 0 until address.maxAddressLineIndex) {
                    addressText += if (i == 0) address.getAddressLine(i) else "\n" + address.getAddressLine(
                        i
                    )
                }
            }
        } catch (e: IOException) {
            Log.e("MapsActivity", e.localizedMessage)
        }
        return addressText
    }

    override fun onMapClick(p0: LatLng) {
        googleMap.clear()
        placeMarkerOnMap(p0)
    }


}