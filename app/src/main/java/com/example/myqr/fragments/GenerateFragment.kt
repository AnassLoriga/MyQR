package com.example.myqr.fragments

import MapsFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.example.myqr.R

/**
 * Fragment responsible for generating different types of QR codes.
 * This fragment provides options to create QR codes for links, contacts, Wi-Fi, and locations.
 */
class GenerateFragment : Fragment() {

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.generate_layout, container, false)
    }

    /**
     * Called immediately after `onCreateView()`.
     * Used to initialize UI components and set click listeners.
     *
     * @param view The View returned by `onCreateView()`.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Finding UI elements by their IDs
        val link = view.findViewById<LinearLayout>(R.id.link)
        val contact = view.findViewById<LinearLayout>(R.id.contact)
        val wifi = view.findViewById<LinearLayout>(R.id.wifi)
        val location = view.findViewById<LinearLayout>(R.id.location)

        // Opens dialog for generating a QR code for a link
        link.setOnClickListener {
            val bottomSheetFragment = GenerateDialogLink()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        // Opens dialog for generating a QR code for contact information
        contact.setOnClickListener {
            val bottomSheetFragment = GenerateDialogContact()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        // Opens dialog for generating a QR code for Wi-Fi credentials
        wifi.setOnClickListener {
            val bottomSheetFragment = GenerateDialogWifi()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }

        // Opens a map fragment for generating a QR code for a location
        location.setOnClickListener {
            val bottomSheetFragment = MapsFragment()
            bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
        }
    }
}