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
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.myqr.Data.Historique
import com.example.myqr.Data.TypeHistorique
import com.example.myqr.Function.hideKeyboard
import com.example.myqr.Function.saveQRCodeToGallery
import com.example.myqr.R
import com.example.myqr.Service.HistoriqueService
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class GenerateDialogWifi : DialogFragment() {
    lateinit var bitmap: Bitmap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment_wifi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editTextSSID = view.findViewById<EditText>(R.id.editTextSSID)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        val editTextAuthType = view.findViewById<EditText>(R.id.editTextAuthType)
        val imageViewQR = view.findViewById<ImageView>(R.id.imageViewQR)
        val enregistrer = view.findViewById<Button>(R.id.btnEnregistrer)
        val anuller = view.findViewById<Button>(R.id.btnAnuller)

        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
            imageViewQR.visibility = View.VISIBLE
            enregistrer.visibility =View.VISIBLE
            hideKeyboard(requireContext(),view)
            val ssid = editTextSSID.text.toString()
            val password = editTextPassword.text.toString()
            val authType = editTextAuthType.text.toString()

            if (ssid.isNotEmpty() && authType.isNotEmpty()) {
                    val wifiString = "WIFI:T:$authType;S:$ssid;P:$password;;"

                    val writer = QRCodeWriter()
                    val bitMatrix = writer.encode(wifiString, BarcodeFormat.QR_CODE, 512, 512)
                    bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
                    for (x in 0 until 512) {
                        for (y in 0 until 512) {
                            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    imageViewQR.setImageBitmap(bitmap)
                    HistoriqueService.addHistorique(Historique(bitmap,TypeHistorique.GENERER,wifiString))
            }else {
                Toast.makeText(requireContext(), "vous devez remplir les champs", Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
        }
        anuller.setOnClickListener {
            dismiss()
        }
        enregistrer.setOnClickListener {
            saveQRCodeToGallery(bitmap, editTextSSID.text.toString(), requireContext())
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
