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

/**
 * WelcomeActivity est l'activité d'accueil de l'application.
 * Elle gère la vérification des permissions de la caméra et la navigation vers MainActivity.
 */
class WelcomeActivity : AppCompatActivity() {

    /**
     * Bouton permettant de passer à l'activité principale après vérification des permissions.
     */
    private lateinit var btnGo: Button

    /**
     * Lanceur de demande de permission pour la caméra.
     * Si la permission est accordée, l'utilisateur est redirigé vers MainActivity.
     * Sinon, un message d'erreur est affiché.
     */
    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                navigateToMainActivity()
            } else {
                showPermissionDeniedMessage()
            }
        }

    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise l'interface utilisateur et configure l'écouteur de clic sur le bouton.
     *
     * @param savedInstanceState L'état précédent de l'activité s'il existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_layout)
        window.statusBarColor = ContextCompat.getColor(this, R.color.welcome)

        btnGo = findViewById(R.id.btn_go)
        btnGo.setOnClickListener {
            if (isCameraPermissionGranted()) {
                navigateToMainActivity()
            } else {
                requestCameraPermission()
            }
        }
    }

    /**
     * Vérifie si la permission d'utiliser la caméra est accordée.
     *
     * @return true si la permission est accordée, false sinon.
     */
    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Demande la permission d'utiliser la caméra à l'utilisateur.
     * Affiche un dialogue explicatif si nécessaire.
     */
    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            showPermissionRationaleDialog()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    /**
     * Affiche un dialogue expliquant pourquoi la permission de la caméra est nécessaire.
     */
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

    /**
     * Affiche un message informant l'utilisateur que la permission de la caméra est nécessaire.
     */
    private fun showPermissionDeniedMessage() {
        android.widget.Toast.makeText(
            this,
            "Camera permission is required to use this feature.",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Redirige l'utilisateur vers l'activité principale (MainActivity).
     */
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
