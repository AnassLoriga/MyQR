package com.example.myqr.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.myqr.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class GenerateTextBootomSheeet: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.generate_text_bootom_sheet, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editText = view.findViewById<EditText>(R.id.editText)
        val imageViewQR = view.findViewById<ImageView>(R.id.imageViewQR)

        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
            val text = editText.text.toString()
            if (text.isNotEmpty()) {
                try {
                    val writer = QRCodeWriter()
                    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
                    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
                    for (x in 0 until 512) {
                        for (y in 0 until 512) {
                            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    imageViewQR.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }
}