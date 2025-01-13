package com.example.myqr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.myqr.R
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.BarcodeView
import com.journeyapps.barcodescanner.camera.CameraSettings

class ScanFragment : Fragment() {
    private lateinit var barcodeScanner: BarcodeView
    private lateinit var btnFlashlight: ImageButton
    private var isFlashOn: Boolean = false
    private var hasScanned: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.camscan_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeScanner = view.findViewById(R.id.barcode_scanner)
        btnFlashlight = view.findViewById(R.id.btn_flashlight)

        val cameraSettings = CameraSettings()
        cameraSettings.isAutoFocusEnabled = true
        barcodeScanner.cameraSettings = cameraSettings

        barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (!hasScanned) {
                    hasScanned = true
                    val scannedText = result.text
                    if (!scannedText.isNullOrEmpty()) {
                        val bottomSheet = GenerateResultBottomSheetFragment()

                        val args = Bundle()
                        args.putString("SCANNED_RESULT", scannedText)
                        bottomSheet.arguments = args
                        bottomSheet.show(parentFragmentManager, "GenerateResultBottomSheetFragment")
                    }
                }
            }

            override fun possibleResultPoints(resultPoints: List<com.google.zxing.ResultPoint>) {}
        })

        btnFlashlight.setOnClickListener {
            toggleFlashlight()
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
