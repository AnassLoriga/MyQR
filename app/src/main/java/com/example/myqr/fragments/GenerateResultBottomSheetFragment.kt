package com.example.myqr.fragments
import android.content.Intent
import android.net.*
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.example.myqr.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenerateResultBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.generate_result_bootom_sheet, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val resultTextView: TextView = view.findViewById(R.id.scan_result)
        val scanTypeTextView: TextView = view.findViewById(R.id.scan_type)
        val ssidTextView: TextView = view.findViewById(R.id.ssid)
        val passwordTextView: TextView = view.findViewById(R.id.password)
        val actionButton: AppCompatButton = view.findViewById(R.id.action_button)

        val scannedResult = arguments?.getString("SCANNED_RESULT") ?: "No result"

        when {
            scannedResult.startsWith("wifi", ignoreCase = true) -> {
                val wifiDetails = parseWiFiDetails(scannedResult)
                scanTypeTextView.text = "WiFi : "
                resultTextView.text = scannedResult
                ssidTextView.text = "SSID: ${wifiDetails["S"] ?: "Not found"}"
                passwordTextView.text = "Password: ${wifiDetails["P"] ?: "Not found"}"
                actionButton.text = "Connect to Wi-Fi"
                actionButton.setOnClickListener {
                    // Blast lwifi
                }
            }
            scannedResult.startsWith("https", ignoreCase = true) -> {
                scanTypeTextView.text = "Link : "
                resultTextView.text = scannedResult
                ssidTextView.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                actionButton.text = "Open Link"
                actionButton.setOnClickListener {
                    openLink(scannedResult)
                }
            }
            scannedResult.startsWith("tel:", ignoreCase = true) -> {
                scanTypeTextView.text = "Phone : "
                resultTextView.text = scannedResult.removePrefix("tel:")
                ssidTextView.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                actionButton.text = "Call Number"
                actionButton.setOnClickListener {
                    openPhoneDialer(scannedResult)
                }
            }
            scannedResult.startsWith("geo:", ignoreCase = true) -> {
                scanTypeTextView.text = "Location : "
                resultTextView.text = scannedResult
                ssidTextView.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                actionButton.text = "Open in Maps"
                actionButton.setOnClickListener {
                    openLocation(scannedResult)
                }
            }
            else -> {
                scanTypeTextView.text = "Text : "
                resultTextView.text = scannedResult
                ssidTextView.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                actionButton.text = "Copy Text"
                actionButton.setOnClickListener {
                    copyToClipboard(scannedResult)
                }
            }

        }


        val parentView = view.parent as View
        val bottomSheetBehavior = BottomSheetBehavior.from(parentView)
        bottomSheetBehavior.peekHeight = 600
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun parseWiFiDetails(result: String): Map<String, String> {
        val details = mutableMapOf<String, String>()
        if (result.startsWith("WIFI:", ignoreCase = true)) {
            val params = result.removePrefix("WIFI:").split(";")
            for (param in params) {
                val keyValue = param.split(":", limit = 2)
                if (keyValue.size == 2) {
                    details[keyValue[0].uppercase()] = keyValue[1]
                }
            }
        }
        return details
    }




    private fun openLink(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }

    private fun openPhoneDialer(phoneNumber: String) {
        val phoneNumberWithoutPrefix = phoneNumber.removePrefix("tel:")
        val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumberWithoutPrefix"))
        startActivity(phoneIntent)
    }


    private fun openLocation(geoUri: String) {
        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }


    private fun copyToClipboard(text: String) {
        val clipboardManager = requireContext().getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = android.content.ClipData.newPlainText("Scanned Result", text)
        clipboardManager.setPrimaryClip(clipData)
        android.widget.Toast.makeText(requireContext(), "Text copied to clipboard", android.widget.Toast.LENGTH_SHORT).show()
    }


}
