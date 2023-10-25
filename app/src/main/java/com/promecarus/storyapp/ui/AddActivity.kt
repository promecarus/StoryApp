package com.promecarus.storyapp.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.CAMERA
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.net.Uri
import android.net.Uri.EMPTY
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.promecarus.storyapp.R.string.error_complete_all_input
import com.promecarus.storyapp.R.string.success_add_story
import com.promecarus.storyapp.R.string.wait
import com.promecarus.storyapp.databinding.ActivityAddBinding
import com.promecarus.storyapp.ui.viewmodel.AddViewModel
import com.promecarus.storyapp.utils.ActivityUtils.handleMessage
import com.promecarus.storyapp.utils.ImageUtils.getImageUri
import com.promecarus.storyapp.utils.State.Default
import com.promecarus.storyapp.utils.State.Error
import com.promecarus.storyapp.utils.State.Loading
import com.promecarus.storyapp.utils.State.Success
import com.promecarus.storyapp.utils.ViewModelFactory.Companion.getInstance
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private val viewModel by viewModels<AddViewModel> { getInstance(this) }
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var location: Location = Location("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        observeViewModel()
    }

    private fun setupAction() {
        var currentImageUri: Uri = EMPTY
        val launcherGallery = registerForActivityResult(PickVisualMedia()) {
            it?.let {
                currentImageUri = it
                binding.ivPreview.setImageURI(it)
            }
        }
        val launcherIntentCamera = registerForActivityResult(TakePicture()) {
            if (it) binding.ivPreview.setImageURI(currentImageUri)
        }
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(this@AddActivity, MainActivity::class.java).apply {
                    flags =
                        FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NO_ANIMATION
                })
            }
        }

        onBackPressedDispatcher.addCallback(this, backCallback)

        binding.topAppBar.setNavigationOnClickListener { backCallback.handleOnBackPressed() }

        fusedLocationClient = getFusedLocationProviderClient(this)

        if (!checkCameraPermission() && !checkLocationPermission()) requestPermissions()

        binding.btnCamera.setOnClickListener {
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }

        binding.btnGallery.setOnClickListener {
            launcherGallery.launch(PickVisualMediaRequest(ImageOnly))
        }

        binding.smLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) getLocation()
        }

        binding.buttonAdd.setOnClickListener {
            if (!EMPTY.equals(currentImageUri) && !binding.edAddDescription.text.isNullOrEmpty()) {
                viewModel.addStory(this,
                    binding.edAddDescription.text.toString(),
                    currentImageUri,
                    location.latitude.takeIf { binding.smLocation.isChecked },
                    location.longitude.takeIf { binding.smLocation.isChecked })
            } else makeText(this, error_complete_all_input, LENGTH_LONG).show()
        }
    }

    private fun observeViewModel() {
        val stayCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                makeText(this@AddActivity, wait, LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.state.collect {
                when (it) {
                    is Loading -> {
                        onBackPressedDispatcher.addCallback(this@AddActivity, stayCallback)
                        binding.topAppBar.setNavigationOnClickListener { stayCallback.handleOnBackPressed() }
                        binding.btnCamera.isEnabled = false
                        binding.btnGallery.isEnabled = false
                        binding.edAddDescription.isEnabled = false
                        binding.buttonAdd.isEnabled = false
                        binding.smLocation.isEnabled = false
                        binding.progressBar.visibility = VISIBLE
                    }

                    is Success -> {
                        startActivity(Intent(this@AddActivity, MainActivity::class.java))
                        makeText(this@AddActivity, success_add_story, LENGTH_SHORT).show()
                        finish()
                    }

                    is Error -> handleMessage(this@AddActivity, it.error)

                    is Default -> binding.progressBar.visibility = GONE
                }
            }
        }
    }

    private fun checkCameraPermission() = checkSelfPermission(this, CAMERA) == PERMISSION_GRANTED

    private fun checkLocationPermission() = (checkSelfPermission(
        this, ACCESS_COARSE_LOCATION
    ) == PERMISSION_GRANTED) && checkSelfPermission(
        this, ACCESS_FINE_LOCATION
    ) == PERMISSION_GRANTED

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (!checkCameraPermission()) permissionsToRequest.add(CAMERA)

        if (!checkLocationPermission()) {
            permissionsToRequest.add(ACCESS_COARSE_LOCATION)
            permissionsToRequest.add(ACCESS_FINE_LOCATION)
        }

        if (permissionsToRequest.isNotEmpty()) ActivityCompat.requestPermissions(
            this, permissionsToRequest.toTypedArray(), 100
        )
    }

    private fun getLocation() {
        if (checkLocationPermission()) fusedLocationClient.lastLocation.addOnSuccessListener {
            location = it
        } else requestPermissions()
    }
}
