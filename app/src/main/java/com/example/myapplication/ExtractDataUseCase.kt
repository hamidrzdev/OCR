package com.example.myapplication
import android.media.Image
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer

@OptIn(ExperimentalGetImage::class)

class ExtractDataUseCase(private val textRecognizer: TextRecognizer) {
    private val TAG = "ExtractDataUseCase"
    fun process(imageProxy:ImageProxy,onSuccess:(String)->Unit,onFailure:(Exception)->Unit) {
        if (imageProxy.image==null) return
        val imageInput = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
        val p = textRecognizer.process(imageInput)
        p.addOnSuccessListener {
            imageProxy.close()
            onSuccess.invoke(it.text)
        }
        p.addOnFailureListener {
            imageProxy.close()
            onFailure.invoke(it)
        }
    }

}