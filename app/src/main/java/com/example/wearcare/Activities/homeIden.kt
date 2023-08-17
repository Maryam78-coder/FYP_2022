package com.example.wearcare.Activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wearcare.Model
import com.example.wearcare.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class homeIden : AppCompatActivity() {

    private val REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_homeiden)

        var button = findViewById(R.id.button) as Button


        button.setOnClickListener {
            checkPermission(Manifest.permission.CAMERA, REQUEST_CODE)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            } else
            {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else
        {
            captureImage()
        }
    }

    private fun captureImage(){

        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun uploadImage(bitmap: Bitmap) {

        var progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Saving Image....")
        progressDialog.show()

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CANADA)
        val now = Date()
        val  name = formatter.format(now)

        //taking bitmap data to upload image to firebase

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val database = FirebaseDatabase.getInstance().getReference("images")
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://wearncare-3639d.appspot.com")
        val imagesRef = storageRef.child("images/$name")
        val uploadTask = imagesRef.putBytes(data)


        uploadTask.addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this@homeIden, "Image Saved", Toast.LENGTH_SHORT).show()
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful()) {
                    throw task.getException()!!
                }
                imagesRef.getDownloadUrl()

            }.addOnCompleteListener(OnCompleteListener<Uri?> { task ->
                if (task.isSuccessful) {
                    // Url to add firebase image to realtime database
                    val downUri: Uri? = task.getResult()
                    val upload = Model(downUri.toString())
                    val uploadId: String? = database.push().getKey()
                    if (uploadId != null) {
                        database.child(uploadId).setValue(upload)
                    }
                    Log.d("Final URL", "onComplete: Url: $downUri")
                }
            })
        }.addOnFailureListener { e ->
            progressDialog.dismiss()
            Toast.makeText(this@homeIden, "Failed to Save " + e.message, Toast.LENGTH_SHORT).show()
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

            var bitmap = data?.extras?.get("data") as Bitmap
            Bitmap.createScaledBitmap(
                    bitmap,
                    500,
                    (bitmap.height / (bitmap.width / 500F)).toInt(),
                    true
            )
                    .also { bitmap = it }

            var imageview = findViewById(R.id.imageview) as ImageView

            imageview.setImageBitmap(bitmap)
            // to upload image to firebase
            uploadImage(bitmap)

            val inputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            // Get a Paint instance
            val myRectPaint = Paint()
            myRectPaint.strokeWidth = 2F
            myRectPaint.style = Paint.Style.STROKE

            // Create a canvas using the dimensions from the image's bitmap
            val tempBitmap =
                    Bitmap.createBitmap(inputBitmap.width, inputBitmap.height, Bitmap.Config.RGB_565)
            val tempCanvas = Canvas(tempBitmap)
            tempCanvas.drawBitmap(inputBitmap, 0F, 0F, null)

            // Create a FaceDetector
            val faceDetector = FaceDetector.Builder(applicationContext).setTrackingEnabled(false)
                    .build()
            if (!faceDetector.isOperational) {
                AlertDialog.Builder(this)
                        .setMessage("Could not set up the face detector!")
                        .show()
                return
            }
            // Detect the faces
            val frame = Frame.Builder().setBitmap(inputBitmap).build()
            val faces = faceDetector.detect(frame)

            // Mark out the identified face
            for (i in 0 until faces.size()) {
                val thisFace = faces.valueAt(i)
                val left = thisFace.position.x
                val top = thisFace.position.y
                val right = left + thisFace.width
                val bottom = top + thisFace.height
                val bitmapCropped = Bitmap.createBitmap(inputBitmap, left.toInt(), top.toInt(),
                        if (right.toInt() > inputBitmap.width) {
                            inputBitmap.width - left.toInt()
                        } else {
                            thisFace.width.toInt()
                        },
                        if (bottom.toInt() > inputBitmap.height) {
                            inputBitmap.height - top.toInt()
                        } else {
                            thisFace.height.toInt()
                        }
                )
                val label = predict(bitmapCropped)
                var prediction = ""
                val with = label["WithMask"] ?: 0F
                val without = label["WithoutMask"] ?: 0F

                if (with > without) {
                    myRectPaint.setColor(Color.GREEN)
                    prediction = "With Mask : " + String.format("\n%.1f", with * 100) + "%"
//                    result.setText(prediction)
                } else {
                    myRectPaint.setColor(Color.RED)
                    prediction = "Without Mask : " + String.format("\n%.1f", without * 100) + "%"
//                    result.setText(prediction)
                }
                myRectPaint.setTextSize(thisFace.width / 8)
                myRectPaint.setTextAlign(Paint.Align.LEFT)
                tempCanvas.drawText(prediction, left, top - 9F, myRectPaint)
                tempCanvas.drawRoundRect(RectF(left, top, right, bottom), 2F, 2F, myRectPaint)
            }
            imageview.setImageDrawable(BitmapDrawable(resources, tempBitmap))
            // Release the FaceDetector
            faceDetector.release()
        }
    }

    private fun predict(input: Bitmap): MutableMap<String, Float> {
        // load model
        val modelFile = FileUtil.loadMappedFile(applicationContext, "model.tflite")
        val model = Interpreter(modelFile, Interpreter.Options())
        val labels = FileUtil.loadLabels(applicationContext, "labels.txt")

        // data type
        val imageDataType = model.getInputTensor(0).dataType()
        val inputShape = model.getInputTensor(0).shape()

        val outputDataType = model.getOutputTensor(0).dataType()
        val outputShape = model.getOutputTensor(0).shape()

        var inputImageBuffer = TensorImage(imageDataType)
        val outputBuffer = TensorBuffer.createFixedSize(outputShape, outputDataType)

        // preprocess
        val cropSize = kotlin.math.min(input.width, input.height)
        val imageProcessor = ImageProcessor.Builder()
                .add(ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(ResizeOp(inputShape[1], inputShape[2], ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(NormalizeOp(127.5f, 127.5f))
                .build()

        // load image
        inputImageBuffer.load(input)
        inputImageBuffer = imageProcessor.process(inputImageBuffer)

        // run model
        model.run(inputImageBuffer.buffer, outputBuffer.buffer.rewind())


        // get output
        val labelOutput = TensorLabel(labels, outputBuffer)

        val label = labelOutput.mapWithFloatValue

        val confidences: FloatArray = outputBuffer.getFloatArray()
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }

        val classes = arrayOf("With Mask", "Without Mask")
        var result = findViewById(R.id.result) as TextView
        result.text = classes[maxPos]

        var s = ""
        for (i in classes.indices) {
            s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100)
        }
        var confidence = findViewById(R.id.confidence) as TextView
        confidence.text = s

        return label
    }
}