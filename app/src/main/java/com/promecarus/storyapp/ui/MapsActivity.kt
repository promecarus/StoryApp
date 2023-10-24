package com.promecarus.storyapp.ui

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions.loadRawResourceStyle
import com.google.android.gms.maps.model.MarkerOptions
import com.promecarus.storyapp.R.id.map
import com.promecarus.storyapp.R.raw.map_style
import com.promecarus.storyapp.databinding.ActivityMapsBinding
import com.promecarus.storyapp.ui.viewmodel.MapsViewModel
import com.promecarus.storyapp.utils.State
import com.promecarus.storyapp.utils.ViewModelFactory
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> { ViewModelFactory.getInstance(this) }
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            uiSettings.isCompassEnabled = true
            uiSettings.isMapToolbarEnabled = true
            setMapStyle(loadRawResourceStyle(this@MapsActivity, map_style))
        }

        getPermission()
        observeViewModel()
    }

    private fun getPermission() {
        if (checkSelfPermission(
                this.applicationContext, ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) mMap.isMyLocationEnabled = true
        else registerForActivityResult(RequestPermission()) {
            if (it) mMap.isMyLocationEnabled = true
        }.launch(ACCESS_FINE_LOCATION)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is State.Loading -> {}

                    is State.Success -> {
                        it.data.forEach { story ->
                            if (story.lat != null && story.lon != null) {
                                mMap.addMarker(
                                    MarkerOptions().position(LatLng(story.lat, story.lon))
                                        .title(story.name).snippet(story.description).flat(true)
                                )
                            }
                        }
                    }

                    is State.Error -> {}

                    is State.Default -> {}
                }
            }
        }
    }
}
