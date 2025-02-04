package com.example.myqr.fragments
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.*
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.FileProvider
import com.example.myqr.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream

class GenerateResultBottomSheetFragment : BottomSheetDialogFragment() {
    lateinit var scannedResult :String

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
        val PartagerButton: AppCompatButton = view.findViewById(R.id.partager_button)

        scannedResult = arguments?.getString("SCANNED_RESULT") ?: "No result"

        when {
            scannedResult.startsWith("wifi", ignoreCase = true) -> {
                val wifiDetails = parseWiFiDetails(scannedResult)
                scanTypeTextView.text = "WiFi : "
                resultTextView.text = scannedResult
                ssidTextView.text = "SSID: ${wifiDetails["S"] ?: "Not found"}"
                passwordTextView.text = "Password: ${wifiDetails["P"] ?: "Not found"}"
                actionButton.text = "Connect to Wi-Fi"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    actionButton.setOnClickListener {
                        val ssid = wifiDetails["S"] ?: return@setOnClickListener
                        val password = wifiDetails["P"] ?: ""

                        val wifiManager = requireContext().applicationContext.getSystemService(
                            Context.WIFI_SERVICE) as WifiManager
                        val wifiConfig = WifiConfiguration().apply {
                            SSID = "\"$ssid\""
                            preSharedKey = "\"$password\""
                        }

                        val netId = wifiManager.addNetwork(wifiConfig)
                        if (netId != -1) {
                            wifiManager.disconnect()
                            wifiManager.enableNetwork(netId, true)
                            wifiManager.reconnect()
                        } else {
                            Toast.makeText(requireContext(), "Failed to configure Wi-Fi", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Wi-Fi connection requires API 29 or higher", Toast.LENGTH_SHORT).show()
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
        PartagerButton.setOnClickListener {
            shareBitmapAndText(scannedResult)
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
        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    private fun shareBitmapAndText(text: String) {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        for (x in 0 until 512) {
            for (y in 0 until 512) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
        val imageUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "shared_image.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/SharedImages")
            }
            val uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                requireContext().contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            }
            uri
        } else {
            val imagesDir = requireContext().externalCacheDir
            val image = File(imagesDir, "shared_image.jpg")
            image.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            Uri.fromFile(image)
        }

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        startActivity(Intent.createChooser(shareIntent, "Partager via"))
    }



}
