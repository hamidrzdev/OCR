package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withCreated
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val TAG = "MainActivity"
    private val viewModel: MainActivityVm by viewModels()
    private var requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            viewModel.permissionFlow.value = isGranted
            if (isGranted) {
                Log.i(TAG, "requestCameraPermissionLauncher: user already have permission")
                lifecycleScope.launchWhenStarted {
                    prepareCamera()
                }
            } else {
                Log.i(TAG, "requestCameraPermissionLauncher: user does not have camera permission")
            }
        }

    lateinit var binding: ActivityMainBinding

    private lateinit var providers: Providers
    private val extractDataUseCase =
        ExtractDataUseCase(TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        checkCameraPermission()
        observer()


    }

    private fun initView() {
        providers = Providers(binding.camera)
    }

    private fun observer() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.permissionFlow.collect {
                    Log.d(TAG, "observer: permission flow: $it")

                    if (it) {
                        prepareCamera()
                    }
                }
            }
        }
    }

    private suspend fun prepareCamera() {
        Log.i(TAG, "prepareCamera: ")
        val cameraProvider = providers.getCameraProvider(this@MainActivity)
        cameraProvider.bindToLifecycle(
            this@MainActivity,
            providers.cameraSelector,
            providers.buildPreview,
            providers.buildTakePicture
        )

        // use multiple coroutines to have more scans ????!!!
        CoroutineScope(Dispatchers.Default).launch {
            repeat(Int.MAX_VALUE) {
                startCapture()
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private suspend fun startCapture() {
        Log.i(TAG, "startCapture: ")
        val imageProxy = providers.buildTakePicture.takePicture(ContextCompat.getMainExecutor(this))
        extractDataUseCase.process(imageProxy, onSuccess = {
            Log.i(TAG, "startCapture: success: $it")
        }, onFailure = {
            Log.e(TAG, "startCapture: failure: ", it)
        })
    }

    private fun checkCameraPermission(/*execute: () -> Unit*/) {
        val havePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        viewModel.permissionFlow.value = havePermission
        Log.i(TAG, "checkCameraPermission: havePermission: $havePermission")
        if (havePermission) {
        } else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)

    }
}