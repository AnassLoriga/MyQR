package com.example.myqr.fragments

import MapsFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.myqr.R

class GenerateFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.generate_layout, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val link = view.findViewById<Button>(R.id.link)
        val contact = view.findViewById<Button>(R.id.Contact)
        val wifi = view.findViewById<Button>(R.id.Wifi)
        val location = view.findViewById<Button>(R.id.Location)
        link.setOnClickListener {
            val bottomSheetFragment = GenerateDialogLink()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
        contact.setOnClickListener {
            val bottomSheetFragment = GenerateDialogContact()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
        wifi.setOnClickListener {
            val bottomSheetFragment = GenerateDialogWifi()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
        location.setOnClickListener {
            val bottomSheetFragment = MapsFragment()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }


    }
}