package com.example.mealmate.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mealmate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {
    private lateinit var tvEmail: TextView
    private lateinit var etPhoneNumber: EditText
    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var userProfileImage: ImageView
    private lateinit var btnSave: Button
    private lateinit var btnChangeProfilePic: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val storage = FirebaseStorage.getInstance()
    private val PICK_IMAGE_REQUEST = 1
    private val CAMERA_REQUEST = 2
    private var imageUri: Uri? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize views
        tvEmail = findViewById(R.id.tvEmail)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etName = findViewById(R.id.etName)
        etAddress = findViewById(R.id.etAddress)
        userProfileImage = findViewById(R.id.userProfile)
        btnSave = findViewById(R.id.btnSave)
        btnChangeProfilePic = findViewById(R.id.btnChangeProfilePic)

        // Display user's email
        currentUser?.let {
            tvEmail.text = "Email: ${it.email}"
            loadUserData(it.uid)
        }

        // Change profile picture button click
        btnChangeProfilePic.setOnClickListener {
            checkPermissionsAndOpenChooser()
        }

        // Save button click listener
        btnSave.setOnClickListener {
            saveUserData()
        }
    }

    // Load user data (phone number, name, address, and profile picture)
    private fun loadUserData(userId: String) {
        firestore.collection("Users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    etPhoneNumber.setText(document.getString("phoneNumber"))
                    etName.setText(document.getString("name"))
                    etAddress.setText(document.getString("address"))
                    val profilePicUrl = document.getString("profilePicUrl")
                    if (profilePicUrl != null) {
                        Glide.with(this).load(profilePicUrl).into(userProfileImage)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
    }

    // Check permissions and open image chooser
    private fun checkPermissionsAndOpenChooser() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                100
            )
        } else {
            openImageChooser() // Directly open chooser if permissions are already granted
        }
    }

    // Open image chooser dialog
    private fun openImageChooser() {
        val options = arrayOf("Select from Gallery", "Take a Photo")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    // Open the gallery to choose an image
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Open the camera to take a new photo
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, CAMERA_REQUEST)
    }

    // Handle result from gallery or camera
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUri = data?.data
                    if (imageUri != null) {
                        Glide.with(this).load(imageUri).into(userProfileImage)
                    }
                }
                CAMERA_REQUEST -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        imageUri = getImageUriFromBitmap(it)
                        Glide.with(this).load(imageUri).into(userProfileImage)
                    }
                }
            }
        }
    }

    // Convert Bitmap to Uri
    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "ProfilePic", null)
        return Uri.parse(path)
    }

    // Save user data (name, phone number, address, and profile picture URL)
    private fun saveUserData() {
        val name = etName.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val address = etAddress.text.toString().trim()

        if (name.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri != null) {
            val storageRef = storage.reference.child("profile_pictures/${currentUser?.uid}")
            storageRef.putFile(imageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveToFirestore(name, phoneNumber, address, uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        } else {
            saveToFirestore(name, phoneNumber, address, null)
        }
    }

    // Save data to Firestore
    private fun saveToFirestore(name: String, phoneNumber: String, address: String, profilePicUrl: String?) {
        val userMap = hashMapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "address" to address,
            "profilePicUrl" to profilePicUrl
        )

        currentUser?.let {
            firestore.collection("Users").document(it.uid).set(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Handle permission request results
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                // Check if permissions are granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    openImageChooser() // Open chooser if permissions are granted
                } else {
                    Toast.makeText(this, "Permissions denied. Cannot change profile picture.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
