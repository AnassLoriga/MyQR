package com.example.myqr.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myqr.Function.hideKeyboard
import com.example.myqr.Function.saveQRCodeToGallery
import com.example.myqr.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class GenerateDialogContact : DialogFragment() {
    lateinit var bitmap: Bitmap
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextSimNumber = view.findViewById<EditText>(R.id.editText)
        val imageViewQR = view.findViewById<ImageView>(R.id.imageViewQR)
        val enregistrer = view.findViewById<Button>(R.id.btnEnregistrer)
        val anuller = view.findViewById<Button>(R.id.btnAnuller)
        val linearDialog = view.findViewById<LinearLayout>(R.id.linearDialo)


        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
            val simNumber = editTextSimNumber.text.toString()
            hideKeyboard(requireContext(),view)
            if (simNumber.isNotEmpty()) {
                    enregistrer.visibility=View.VISIBLE
                    imageViewQR.visibility = View.VISIBLE
                    linearDialog.setBackgroundResource(R.drawable.dialog_background)
                    val writer = QRCodeWriter()
                    val bitMatrix = writer.encode(simNumber, BarcodeFormat.QR_CODE, 512, 512)
                    bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
                    for (x in 0 until 512) {
                        for (y in 0 until 512) {
                            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    imageViewQR.setImageBitmap(bitmap)
            }else{
                Toast.makeText(requireContext(),"vous devez remplir le champs",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }
        anuller.setOnClickListener {
            dismiss()
        }
        enregistrer.setOnClickListener {
            saveQRCodeToGallery(bitmap, "MyQRCode", requireContext())
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(android.view.Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}
