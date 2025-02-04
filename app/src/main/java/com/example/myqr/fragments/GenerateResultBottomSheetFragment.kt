package com.example.myqr.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import com.example.myqr.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenerateResultBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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

            // wifi qr
            scannedResult.startsWith("WIFI:", ignoreCase = true) -> {
                val wifiDetails = parseWiFiDetails(scannedResult)
                scanTypeTextView.text = "WiFi : "

                val ssid = wifiDetails["S"] ?: "Not found"
                val password = wifiDetails["P"] ?: "Not found"

                ssidTextView.text = "SSID: $ssid"
                passwordTextView.text = "Password: $password"
                actionButton.text = "Connect to Wi-Fi"

                actionButton.setOnClickListener {
                    if (wifiDetails.isNotEmpty()) {
                        connectToWifi(wifiDetails)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Invalid Wi-Fi QR code or unsupported Android version",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            // lien

            scannedResult.startsWith("http", ignoreCase = true) -> {
                scanTypeTextView.text = "Link : "
                resultTextView.text = scannedResult
                ssidTextView.visibility = View.GONE
                passwordTextView.visibility = View.GONE
                actionButton.text = "Open Link"
                actionButton.setOnClickListener {
                    openLink(scannedResult)
                }
            }

            // tele
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
            // localisation

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

            // text
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


    // wifi data
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


    // connect to wifi
    private fun connectToWifi(wifiDetails: Map<String, String>) {
        val ssid = wifiDetails["S"] ?: return
        val password = wifiDetails["P"] ?: ""
        val authType = wifiDetails["T"]?.uppercase() ?: "WPA"

        val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val suggestionBuilder = WifiNetworkSuggestion.Builder().setSsid(ssid)

            when {
                authType.contains("WPA") -> {
                    suggestionBuilder.setWpa2Passphrase(password)
                }
                authType.contains("WPA3") -> {
                    suggestionBuilder.setWpa3Passphrase(password)
                }
                authType.contains("WEP") -> {
                    Toast.makeText(requireContext(), "WEP networks may not be fully supported on Q+", Toast.LENGTH_SHORT).show()
                }
                authType.contains("NOPASS") -> {
                }
            }

            val suggestion = suggestionBuilder.build()
            val suggestionsList = listOf(suggestion)
            val status = wifiManager.addNetworkSuggestions(suggestionsList)

            if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                Toast.makeText(
                    requireContext(),
                    "Wi-Fi connection suggested. Confirm via Wi-Fi settings.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failed to suggest Wi-Fi network (status=$status).",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            val wifiConfig = WifiConfiguration().apply {
                SSID = "\"$ssid\""

                when {
                    authType.contains("WPA") -> {
                        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                        preSharedKey = "\"$password\""
                    }
                    authType.contains("WEP") -> {
                        wepKeys[0] = "\"$password\""
                        wepTxKeyIndex = 0
                        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                        allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                    }
                    authType.contains("NOPASS") -> {
                        allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    }
                }
            }

            val netId = wifiManager.addNetwork(wifiConfig)
            if (netId != -1) {
                wifiManager.disconnect()
                wifiManager.enableNetwork(netId, true)
                wifiManager.reconnect()
                Toast.makeText(requireContext(), "Attempting to connect to $ssid", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to configure Wi-Fi", Toast.LENGTH_SHORT).show()
            }
        }
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
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clipData = android.content.ClipData.newPlainText("Scanned Result", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
