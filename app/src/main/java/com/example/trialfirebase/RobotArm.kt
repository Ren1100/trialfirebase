package com.example.trialfirebase


import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

private const val CAMERA_REQUEST_CODE = 101


class RobotArm : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var db: FirebaseFirestore
    private val containers: MutableList<container> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.robot_arm)

        db = FirebaseFirestore.getInstance()
        setupPermissions()
        codeScanner()
    }


    private fun codeScanner() {
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback{
                runOnUiThread{
                    val textView = findViewById<TextView>(R.id.tv_textView)
                    textView.text = it.text
                    val result = it.text
                    fetchContainerData(result)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera Initialization Error; ${it.message}")
                }
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        "You need the camera permission to be able to use this app",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //successful
                }
            }
        }
    }
    private fun fetchContainerData(result: String) {

        val containerRef = db.collection("results").document(result)
        containerRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val containerData = document.toObject(container::class.java)
                    containerData?.let { container ->
                        containers.add(container)
                        sendLocationToRealtimeDatabase(container.location)
                    }
                } else {
                    Toast.makeText(this, "Container data not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QRScanActivity", "Error fetching container data: ${exception.message}")
                Toast.makeText(this, "Error fetching container data", Toast.LENGTH_SHORT).show()
            }
    }

    private fun sendLocationToRealtimeDatabase(location: Long) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("/location")
        databaseRef.child("location").setValue(location)
            .addOnSuccessListener {
                Toast.makeText(this, "Location sent to Realtime Database", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error sending location to Realtime Database: ${e.message}")
                Toast.makeText(this, "Error sending location to Realtime Database", Toast.LENGTH_SHORT).show()
            }
    }
}