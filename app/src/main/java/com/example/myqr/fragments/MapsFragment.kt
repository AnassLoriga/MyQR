import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
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
import com.example.myqr.Function.shareBitmapAndText
import com.example.myqr.R
import com.example.myqr.Service.HistoriqueService
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

/**
 * Un [DialogFragment] qui génère un code QR basé sur une localisation géographique.
 * Il permet à l'utilisateur d'ouvrir Google Maps, de saisir des coordonnées et de créer un code QR pour le partager ou le sauvegarder.
 */
class MapsFragment : DialogFragment() {

    /** Bitmap représentant le code QR généré. */
    lateinit var bitmap: Bitmap

    /** Le contenu textuel encodé dans le code QR. */
    lateinit var text: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editText = view.findViewById<EditText>(R.id.editText)
        val btnQR = view.findViewById<Button>(R.id.btnGenerateQR)
        val btnenregistrer = view.findViewById<Button>(R.id.btnEnregistrer)
        val imageViewQR = view.findViewById<ImageView>(R.id.imageViewQR)
        val partager = view.findViewById<Button>(R.id.btnPartager)
        val btnOpenMaps: Button = view.findViewById(R.id.OuvrirGoogleMaps)

        /**
         * Ouvre Google Maps pour sélectionner une localisation.
         * Rend les champs de saisie visibles pour entrer des coordonnées.
         */
        btnOpenMaps.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:0,0?q=")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivityForResult(mapIntent, 100)
            editText.visibility = View.VISIBLE
            btnQR.visibility = View.VISIBLE
            partager.visibility = View.VISIBLE
        }

        /**
         * Génère un code QR à partir des coordonnées de localisation fournies.
         * Ajoute le code QR généré au service d'historique.
         */
        btnQR.setOnClickListener {
            imageViewQR.visibility = View.VISIBLE
            btnenregistrer.visibility = View.VISIBLE
            hideKeyboard(requireContext(), view)
            if (editText.text?.isNotEmpty() == true) {
                val Location = editText.text.toString().trim()
                text = "geo URIs ($Location)"

                val writer = QRCodeWriter()
                val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
                bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
                for (x in 0 until 512) {
                    for (y in 0 until 512) {
                        bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                    }
                }
                imageViewQR.setImageBitmap(bitmap)
                HistoriqueService.addHistorique(Historique(R.drawable.ic_location,bitmap, TypeHistorique.GENERER, text))
            } else {
                Toast.makeText(requireContext(), "vous devez remplir les champs", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
        }

        /**
         * Enregistre le code QR généré dans la galerie.
         */
        btnenregistrer.setOnClickListener {
            saveQRCodeToGallery(bitmap, "geoLocalisation", requireContext())
        }

        /**
         * Partage le code QR ainsi que son contenu textuel.
         */
        partager.setOnClickListener {
            shareBitmapAndText(requireContext(), text)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(android.view.Gravity.CENTER)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}