package com.example.myqr.Activitys

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myqr.R
import com.example.myqr.fragments.GenerateFragment
import com.example.myqr.fragments.ScanFragment
import com.example.myqr.fragments.historiqueFragment

/**
 * MainActivity est l'activité principale de l'application.
 * Elle gère la navigation entre les fragments : Scan, Generate et Historique.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Méthode appelée lors de la création de l'activité.
     * Elle initialise l'interface utilisateur et configure les écouteurs de clics pour la navigation entre les fragments.
     *
     * @param savedInstanceState L'état précédent de l'activité s'il existe.
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val generate = findViewById<LinearLayout>(R.id.generate)
        val scan = findViewById<LinearLayout>(R.id.scan)
        val historique = findViewById<LinearLayout>(R.id.historique)

        // Affiche par défaut le fragment de scan
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, ScanFragment())
            .commit()

        // Affiche le fragment de génération de QR code lorsque l'utilisateur clique sur le bouton "Generate"
        generate.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, GenerateFragment())
                .commit()
        }

        // Affiche le fragment de scan lorsque l'utilisateur clique sur le bouton "Scan"
        scan.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, ScanFragment())
                .commit()
        }

        // Affiche le fragment d'historique lorsque l'utilisateur clique sur le bouton "Historique"
        historique.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame, historiqueFragment())
                .commit()
        }
    }
}
