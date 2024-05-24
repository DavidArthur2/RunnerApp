package com.festipay.runnerapp.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.festipay.runnerapp.R
import com.festipay.runnerapp.utilities.CurrentState
import com.festipay.runnerapp.utilities.FragmentType
import com.festipay.runnerapp.utilities.Functions
import com.festipay.runnerapp.utilities.OperationType
import com.festipay.runnerapp.utilities.showError
import com.festipay.runnerapp.utilities.showWarning
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraFragment : Fragment() {

    private lateinit var photoButton: FloatingActionButton
    private lateinit var photoView: ImageView
    private lateinit var context: FragmentActivity

    private val storage = FirebaseStorage.getInstance()
    private lateinit var vFilename: String
    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_camera, container, false)
        initFragment()
        initView(view)
        loadImage()
        return view
    }
    private fun initView(view: View){
        photoView = view.findViewById(R.id.photoView)
        photoButton = view.findViewById(R.id.cameraFloatingActionButton)
        photoButton.setOnClickListener {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "RunnerApp image capturer")
        values.put(MediaStore.Images.Media.DESCRIPTION, "RunnerApp image capturer")
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        vFilename = "${CurrentState.companySiteID}.jpg"

        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(downloadsDir, vFilename)
        val image_uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    openCamera()
                }

            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(downloadsDir, vFilename)
            val uri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            uploadImageToFirebase(bitmap)
        }
    }

    private fun initFragment(){
        context = requireActivity()
        CurrentState.fragmentType = FragmentType.DEMOLITION_COMPANY_CAMERA
        CurrentState.operation = OperationType.CAMERA

        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (!arePermissionsGranted( permissions)) {
            requestPermissions(context, permissions, 123)
        }
    }

    private fun onViewLoaded(){
        Functions.hideLoadingScreen()
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        Functions.showLoadingScreen(context)
        val storageRef = storage.reference
        val imagesRef = storageRef.child("${CurrentState.companySiteID}.jpg")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            showError(context, "Sikertelen képfeltöltés!", it.toString())
        }.addOnSuccessListener { taskSnapshot ->
            photoView.setImageBitmap(bitmap)
            Functions.showInfoDialog(context, "Feltöltés", "Sikeres képfeltöltés!")

        }
    }

    private fun loadImage() {
        val storageRef: StorageReference = storage.reference.child("${CurrentState.companySiteID}.jpg")
        try {
            val localFile = File.createTempFile("${CurrentState.companySiteID}", "jpg")
            storageRef.getFile(localFile).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                photoView.setImageBitmap(bitmap)
            }.addOnFailureListener { exception ->
                showWarning(context, "Nem található elmentett kép!")
            }.addOnCompleteListener{
                onViewLoaded()
            }
        } catch (e: IOException) {
            showError(context,"Hiba történt a kép betöltésekor!")
            onViewLoaded()
        }
    }

    private fun arePermissionsGranted(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
    }
}