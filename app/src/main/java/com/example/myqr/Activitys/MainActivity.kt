package com.example.myqr.Activitys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myqr.R
import com.example.myqr.fragments.ScanFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame,ScanFragment())
            .commit()
    }

}