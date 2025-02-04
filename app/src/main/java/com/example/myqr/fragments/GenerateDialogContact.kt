package com.example.myqr.fragments

import android.graphics.*
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.myqr.Data.Historique
import com.example.myqr.Data.TypeHistorique
import com.example.myqr.Function.hideKeyboard
import com.example.myqr.Function.saveQRCodeToGallery
import com.example.myqr.Function.shareBitmapAndText
import com.example.myqr.R
import com.example.myqr.Service.HistoriqueService
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

/**
 * Fragment de dialogue permettant de générer un QR code à partir d'un numéro de téléphone.
 *
 * Ce fragment offre les fonctionnalités suivantes :
 * - Génération d'un QR code.
 * - Enregistrement du QR code dans la galerie.
 * - Partage du QR code avec du texte.
 */
class GenerateDialogContact : DialogFragment() {

    /**
     * Bitmap contenant l'image du QR code généré.
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

        // Récupération des éléments de l'interface utilisateur
        val editTextSimNumber = view.findViewById<EditText>(R.id.editText)
        val imageViewQR = view.findViewById<ImageView>(R.id.imageViewQR)
        val enregistrer = view.findViewById<ImageButton>(R.id.btnEnregistrer)
        val linearDialog = view.findViewById<LinearLayout>(R.id.linearDialo)
        val partager = view.findViewById<ImageButton>(R.id.btnPartager)

        view.findViewById<Button>(R.id.btnGenerateQR).setOnClickListener {
            simNumber = "Tel: ${editTextSimNumber.text}"
            hideKeyboard(requireContext(), view)

            // Vérifie si le champ est rempli
            if (simNumber.isNotEmpty()) {
                enregistrer.visibility = View.VISIBLE
                imageViewQR.visibility = View.VISIBLE
                partager.visibility = View.VISIBLE
                linearDialog.setBackgroundResource(R.drawable.dialog_background)

                // Générer un QR code avec un design arrondi
                bitmap = generateRoundedQRCode(simNumber, 512)

                // Affichage du QR code généré
                imageViewQR.setImageBitmap(bitmap)

                // Ajout du QR code à l'historique
                HistoriqueService.addHistorique(
                    Historique(R.drawable.ic_contact, bitmap, TypeHistorique.GENERER, simNumber)
                )
            } else {
                Toast.makeText(requireContext(), "Vous devez remplir le champ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        // Enregistrer le QR code dans la galerie
        enregistrer.setOnClickListener {
            saveQRCodeToGallery(bitmap, "MyQRCode", requireContext())
        }

        // Partager le QR code avec du texte
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
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    /**
     * Génère un QR code avec un design arrondi et un rendu professionnel.
     *
     * @param simNumber Texte à encoder dans le QR code.
     * @param size Taille de l'image du QR code.
     * @return Bitmap contenant le QR code généré.
     */
    private fun generateRoundedQRCode(simNumber: String, size: Int): Bitmap {
        val writer = MultiFormatWriter()
        val bitMatrix: BitMatrix = writer.encode(simNumber, BarcodeFormat.QR_CODE, size, size)

        // Création d'un Bitmap et d'un Canvas pour dessiner le QR code
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Peinture pour le QR code avec une couleur plus douce (gris foncé ou bleu foncé)
        val paint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor("#333333") // Gris foncé (ou utilisez "#1E3A8A" pour du bleu foncé)
        }

        // Peinture pour le fond (blanc)
        val backgroundPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.WHITE
        }

        // Dessiner un fond arrondi
        val cornerRadius = size / 8f // Détermine l'arrondi des coins
        canvas.drawRoundRect(RectF(0f, 0f, size.toFloat(), size.toFloat()), cornerRadius, cornerRadius, backgroundPaint)

        // Peinture pour l'effet d'ombre légère
        val shadowPaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = Color.parseColor("#555555") // Ombre plus claire
        }

        // Dessiner les points du QR code sous forme de cercles pour un effet plus moderne
        for (x in 0 until size) {
            for (y in 0 until size) {
                if (bitMatrix[x, y]) {
                    // Dessiner une ombre légèrement décalée pour un effet 3D
                    canvas.drawCircle(x + 1.5f, y + 1.5f, 5f, shadowPaint)
                    canvas.drawCircle(x.toFloat(), y.toFloat(), 5f, paint) // Dessiner le point principal
                }
            }
        }

        return bitmap
    }

}
