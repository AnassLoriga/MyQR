package com.example.myqr.fragments

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
        link.setOnClickListener {
            val bottomSheetFragment = GenerateTextBootomSheeet()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }
}