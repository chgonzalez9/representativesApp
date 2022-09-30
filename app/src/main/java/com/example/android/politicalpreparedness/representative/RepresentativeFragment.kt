package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.example.android.politicalpreparedness.representative.adapter.setNewValue
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.util.Locale

class DetailFragment : Fragment() {

    private lateinit var _requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var _requestLocationSettings: ActivityResultLauncher<IntentSenderRequest>

    private val _locationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(requireContext()) }

    private val _viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this)[RepresentativeViewModel::class.java]
    }

    private lateinit var binding: FragmentRepresentativeBinding

    private val MOTIONLAYOUT_KEY = "motionLayout"
    private val RECYCLER_INDEX_KEY = "recyclerIndex"
    private val ADDRESS_KEY = "address"

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        binding.representativeViewModel = _viewModel

        val adapter = RepresentativeListAdapter()
        binding.representativesList.adapter = adapter

        _viewModel.representatives.observe(viewLifecycleOwner) {
            it?.let {
                adapter.submitList(it)
            }
            try {
                savedInstanceState?.getInt(RECYCLER_INDEX_KEY).let {
                    if (it != null) {
                        binding.representativesList.layoutManager!!.scrollToPosition(it+it)
                    }}
            }catch (e:Exception){
                e.printStackTrace()
            }
        }

        _viewModel.address.observe(viewLifecycleOwner) {
            it?.let {
                _viewModel.getRepresentatives(it.toFormattedString())
                binding.state.setNewValue(it.state)
            }
        }

        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            _viewModel.getAddressFromUser(binding.state.selectedItem as String)
        }

        binding.buttonLocation.setOnClickListener {
            hideKeyboard()
            checkLocationPermissions()
        }

        savedInstanceState?.getParcelable<Address>(ADDRESS_KEY)?.let{
            _viewModel.getAddressFromLocation(it)
        }

        savedInstanceState?.getInt(MOTIONLAYOUT_KEY)?.let{
            binding.representativesMotionLayout.transitionToState(it)
        }

        binding.representativesMotionLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                try {
                    savedInstanceState?.getInt(RECYCLER_INDEX_KEY).let {
                        if (it != null) {
                            binding.representativesList.layoutManager!!.scrollToPosition(it + it)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
        savedInstanceState?.getInt(MOTIONLAYOUT_KEY)?.let {
            binding.representativesMotionLayout.transitionToState(it)
        }

        requestPermissionsResult()
        askPermissions()

        return binding.root

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MOTIONLAYOUT_KEY,binding.representativesMotionLayout.currentState)
        outState.putParcelable(ADDRESS_KEY,binding.representativeViewModel?.address?.value)
        val index: Int = (binding.representativesList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        outState.putInt(RECYCLER_INDEX_KEY,index)
    }

    private fun checkLocationPermissions() {
        return if (isPermissionGranted()) {
            checkDeviceLocationSettingsAndGetLocation()
        } else {
            requestLocationPermissions()
        }
    }

    private fun isPermissionGranted(): Boolean {

        return (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))

    }

    private fun requestLocationPermissions() {

        _requestPermissionLauncher.launch(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private fun requestPermissionsResult() {

        _requestLocationSettings = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()){
            if (it.resultCode == RESULT_OK) {
                getLocation()
            } else {
                _viewModel.showSnackBar.value = getString(R.string.determine_location_error)
            }
        }

    }

    private fun askPermissions() {

        _requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { permission : Boolean ->
            if (permission) {
                checkDeviceLocationSettingsAndGetLocation()
            } else {
                _viewModel.showSnackBar.value = getString(R.string.determine_location_error)
            }
        }

    }

    private fun checkDeviceLocationSettingsAndGetLocation(resolve:Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve){
                try {
                    _requestLocationSettings.launch(IntentSenderRequest.Builder(exception.resolution.intentSender).build())
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            } else {
                _viewModel.showSnackBar.value = getString(R.string.determine_location_error)
            }
        }

        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {

        _locationClient.lastLocation.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                val locationResult = it.result
                locationResult.run {
                    _viewModel.getAddressFromLocation(geoCodeLocation(this))
                }
            }
        }

    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

}