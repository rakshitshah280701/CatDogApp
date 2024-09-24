package com.example.catdogapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import android.content.res.AssetFileDescriptor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import org.tensorflow.lite.Interpreter

class MainActivity : ComponentActivity() {
    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load the TensorFlow Lite model from assets folder
        interpreter = loadModelFile("model.tflite")

        // Initial Content
        setContent {
            var dogProbability by remember { mutableStateOf(0.0f) }
            var catProbability by remember { mutableStateOf(0.0f) }
            var imageUri by remember { mutableStateOf<Uri?>(null) }

            MaterialTheme {
                Scaffold(
                    content = { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            // Button to open gallery
                            Button(onClick = { pickImageFromGallery() }) {
                                Text(text = "Open Gallery")
                            }

                            // Button to open camera
                            Button(onClick = { openCamera() }) {
                                Text(text = "Open Camera")
                            }

                            // UI for displaying results
                            Text(text = "Dog: %.2f%%".format(dogProbability), modifier = Modifier.padding(8.dp))
                            Text(text = "Cat: %.2f%%".format(catProbability), modifier = Modifier.padding(8.dp))
                        }
                    }
                )
            }
        }
    }

    // Function to pick an image from the gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(intent)
    }

    // Gallery result launcher
    private val galleryResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                val bitmap = getBitmapFromUri(uri)
                runInferenceAndUpdateUI(bitmap)
            }
        }
    }

    // Function to open the camera
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResultLauncher.launch(intent)
    }

    // Camera result launcher
    private val cameraResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as Bitmap
            runInferenceAndUpdateUI(bitmap)
        }
    }

    // Function to load the TFLite model from the assets folder
    private fun loadModelFile(modelName: String): Interpreter {
        val fileDescriptor: AssetFileDescriptor = assets.openFd(modelName)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        return Interpreter(mappedByteBuffer)
    }

    // Function to run inference and update the UI
    private fun runInferenceAndUpdateUI(bitmap: Bitmap) {
        val output = runInference(bitmap)
        val dogProbability = output[0] * 100
        val catProbability = output[1] * 100

        setContent {
            MaterialTheme {
                Scaffold(
                    content = { innerPadding ->
                        Column(modifier = Modifier.padding(innerPadding)) {
                            // Button to open gallery
                            Button(onClick = { pickImageFromGallery() }) {
                                Text(text = "Open Gallery")
                            }

                            // Button to open camera
                            Button(onClick = { openCamera() }) {
                                Text(text = "Open Camera")
                            }

                            // UI for displaying results
                            Text(text = "Dog: %.2f%%".format(dogProbability), modifier = Modifier.padding(8.dp))
                            Text(text = "Cat: %.2f%%".format(catProbability), modifier = Modifier.padding(8.dp))
                        }
                    }
                )
            }

        }
    }

    // Function to run inference on an image
    private fun runInference(bitmap: Bitmap): FloatArray {
        val input = preprocessImage(bitmap)
        val output = FloatArray(2)  // Assuming 2-class output for Cat and Dog

        // Run the model inference
        interpreter.run(input, output)

        return output
    }

    // Function to preprocess the bitmap image for the TFLite model
    private fun preprocessImage(bitmap: Bitmap): FloatArray {
        val modelInputSize = 224 // Example size, should match your model's input
        val floatArray = FloatArray(modelInputSize * modelInputSize * 3)

        // Resize the image to match the model input size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, modelInputSize, modelInputSize, true)

        // Normalize the image pixel values to [0, 1]
        var pixel = 0
        for (i in 0 until modelInputSize) {
            for (j in 0 until modelInputSize) {
                val pixelValue = resizedBitmap.getPixel(i, j)

                // Normalize the RGB values from [0, 255] to [0, 1]
                floatArray[pixel++] = ((pixelValue shr 16 and 0xFF) / 255.0f) // Red
                floatArray[pixel++] = ((pixelValue shr 8 and 0xFF) / 255.0f)  // Green
                floatArray[pixel++] = ((pixelValue and 0xFF) / 255.0f)        // Blue
            }
        }
        return floatArray
    }

    // Helper function to convert URI to Bitmap
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
    }
}
