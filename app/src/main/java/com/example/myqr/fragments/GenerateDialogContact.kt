package com.example.myqr.fragments

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import com.example.myqr.Data.Historique
import com.example.myqr.Data.TypeHistorique
import com.example.myqr.Function.hideKeyboard
import com.example.myqr.Function.saveQRCodeToGallery
import com.example.myqr.Function.shareBitmapAndText
import com.example.myqr.R
import com.example.myqr.Service.HistoriqueService
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Fragment de dialogue permettant de générer un QR code à partir d'un numéro de téléphone.
 *
 * Ce fragment offre des fonctionnalités pour :
 * - Générer un QR code.
 * - Enregistrer le QR code dans la galerie.
 * - Partager le QR code avec du texte.
 */
class GenerateDialogContact : DialogFragment() {

    /**
     * Bitmap généré pour le QR code.
     */
    lateinit var bitmap: Bitmap

    /**
     * Numéro de téléphone saisi par l'utilisateur.
     */
    lateinit var simNumber: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fragment, container, false)
    }

    /**
     * Initialise les composants de l'interface utilisateur et configure les actions des boutons.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editTextSimNumber = view.findViewById<EditText>(R.id.editText)
        val imageViewQR = view.findViewById<ImageView>(R.id.imageViewQR)
        val enregistrer = view.findViewById<ImageButton>(R.id.btnEnregistrer)
        val linearDialog = view.findViewById<LinearLayout>(R.id.linearDialo)
        val partager = view.findViewById<ImageButton>(R.id.btnPartager)

        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
            simNumber = "Tel ${editTextSimNumber.text}"
            hideKeyboard(requireContext(), view)

            if (simNumber.isNotEmpty()) {
                enregistrer.visibility = View.VISIBLE
                imageViewQR.visibility = View.VISIBLE
                partager.visibility = View.VISIBLE
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

                // Ajout de l'entrée dans l'historique
                HistoriqueService.addHistorique(
                    Historique(R.drawable.ic_contact,bitmap, TypeHistorique.GENERER, simNumber)
                )
            } else {
                Toast.makeText(requireContext(), "Vous devez remplir le champ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        enregistrer.setOnClickListener {
            saveQRCodeToGallery(bitmap, "MyQRCode", requireContext())
        }

        partager.setOnClickListener {
            shareBitmapAndText(requireContext(), simNumber)
        }
    }

    /**
     * Configure la taille et l'apparence du dialogue lors de son affichage.
     */
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
