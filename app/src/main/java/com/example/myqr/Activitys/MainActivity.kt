package com.example.myqr.Activitys

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myqr.R
import com.example.myqr.fragments.GenerateFragment
import com.example.myqr.fragments.ScanFragment
import com.example.myqr.fragments.historiqueFragment

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val generate = findViewById<LinearLayout>(R.id.generate)
        val scan = findViewById<LinearLayout>(R.id.scan)
        val historique = findViewById<LinearLayout>(R.id.historique)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,ScanFragment())
            .commit()

        generate.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame,GenerateFragment())
                .commit()
        }


        scan.setOnClickListener{
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame,ScanFragment())
                .commit()
        }
        historique.setOnClickListener{
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame,historiqueFragment())
                .commit()
        }
    }

}