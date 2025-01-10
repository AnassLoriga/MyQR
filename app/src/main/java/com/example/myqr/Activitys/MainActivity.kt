package com.example.myqr.Activitys

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.myqr.R
import com.example.myqr.fragments.GenerateFragment
import com.example.myqr.fragments.ScanFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val generate = findViewById<LinearLayout>(R.id.generate)

        generate.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame,GenerateFragment())
                .commit()
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,ScanFragment())
            .commit()
    }

}