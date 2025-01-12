package com.example.myqr.Activitys

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myqr.R

class WelcomeActivity : AppCompatActivity() {
    private lateinit var btnGo: Button

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                navigateToMainActivity()
            } else {
                showPermissionDeniedMessage()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)

        btnGo = findViewById(R.id.btn_go)
        btnGo.setOnClickListener {
            if (isCameraPermissionGranted()) {
                navigateToMainActivity()
            } else {
                requestCameraPermission()
            }
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showPermissionRationaleDialog()
        } else { requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun showPermissionRationaleDialog() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Camera Permission Required")
            .setMessage("This app needs camera access to scan QR codes. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPermissionDeniedMessage() {
        android.widget.Toast.makeText(
            this,
            "Camera permission is required to use this feature.",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
