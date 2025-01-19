package com.example.myqr.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myqr.Data.Historique
import com.example.myqr.Data.TypeHistorique
import com.example.myqr.R
import com.example.myqr.Service.HistoriqueService
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.camera.CameraSettings

class ScanFragment : Fragment() {
    private lateinit var barcodeScanner: BarcodeView
    private lateinit var btnFlashlight: ImageButton
    private lateinit var btnGallery: ImageButton
    private var isFlashOn: Boolean = false
    private var hasScanned: Boolean = false

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.camscan_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeScanner = view.findViewById(R.id.barcode_scanner)
        btnFlashlight = view.findViewById(R.id.btn_flashlight)
        btnGallery = view.findViewById(R.id.btn_galery)

        val cameraSettings = CameraSettings()
        cameraSettings.isAutoFocusEnabled = true
        barcodeScanner.cameraSettings = cameraSettings

        barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (!hasScanned) {
                    handleScannedResult(result.text)
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {}
        })

        btnFlashlight.setOnClickListener {
            toggleFlashlight()
        }

        btnGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage: Uri? = data.data
            selectedImage?.let { uri ->
                val bitmap = getBitmapFromUri(uri)
                bitmap?.let {
                    val qrResult = scanQRCode(it)
                    qrResult?.let {
                        handleScannedResult(it.text)
                    } ?: run {
                        Toast.makeText(requireContext(), "Aucun code QR détecté.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }

    private fun scanQRCode(bitmap: Bitmap): Result? {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        return try {
            MultiFormatReader().decode(binaryBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun handleScannedResult(scannedText: String) {
        hasScanned = true
        if (scannedText.isNotEmpty()) {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(scannedText, BarcodeFormat.QR_CODE, 400, 400)
            HistoriqueService.addHistorique(
                Historique(bitmap, TypeHistorique.SCANNER, scannedText)
            )
            val bottomSheet = GenerateResultBottomSheetFragment()
            val args = Bundle()
            args.putString("SCANNED_RESULT", scannedText)
            bottomSheet.arguments = args
            bottomSheet.show(parentFragmentManager, "GenerateResultBottomSheetFragment")
        }
    }

    private fun toggleFlashlight() {
        isFlashOn = !isFlashOn
        barcodeScanner.setTorch(isFlashOn)

        if (isFlashOn) {
            btnFlashlight.setImageResource(R.drawable.ic_flashlight_on)
        } else {
            btnFlashlight.setImageResource(R.drawable.ic_flashlight_off)
        }
    }

    override fun onResume() {
        super.onResume()
        hasScanned = false
        barcodeScanner.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeScanner.pause()
    }
}
