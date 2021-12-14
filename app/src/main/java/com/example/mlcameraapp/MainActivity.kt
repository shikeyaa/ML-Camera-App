package com.example.mlcameraapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//Create a button that launches the camera app
        findViewById<ImageButton>(R.id.button).setOnClickListener {
            //launch camera app
            //create intent that launches the camera and takes a picture
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            } catch (e: ActivityNotFoundException) {
                // display error state to the user
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Get bitmap from image that was taken
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //Set bitmap as imageView image
            findViewById<ImageView>(R.id.imageView).setImageBitmap(imageBitmap)
            //Prepare bitmap for ML Kit API's
           val imageForMLKit = InputImage.fromBitmap(imageBitmap, 180)
            //Utilize image Labeling API
            val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
            //pass image
            var oPtext = " "
            labeler.process(imageForMLKit)
                .addOnSuccessListener { labels ->
                    Log.i("Keya", "Successfuly processed image" )
                    for (label in labels) {
                        //What was detected in the image
                        val text = label.text
                        //The confidence score of what was detected
                        val confidence = label.confidence
                        val textView = findViewById<TextView>(R.id.textView)
                        oPtext += "$text : $confidence %\n"
                        textView.text = oPtext
                        Log.i("Keya", "detected: " + text + " with confidence:" + confidence)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Keya", "Error processing image")
                }
        }
    }
}

/*Resource:   https://developer.android.com/training/camera/photobasics

            STEPS
1. Obtain image through camera.
2. Prepare image for ML Kit API's.
3. Initialize specific ML Kit API's you want to use (image labeling, text recognition).
4. Feed image to ML Kit API.

C:/Users/shike/AndroidStudioProjects/MLCameraApp
*/