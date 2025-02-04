package com.example.myqr.Data

import android.graphics.Bitmap
import com.example.myqr.R

/**
 * Classe représentant un historique de scan de QR code.
 *
 * @param imageQR L'image du QR code sous forme de bitmap
 * @param type Le type De l'historique sous forme de TypeHistorique (SCANNER,GENERER)
 * @param contenu Le contenu De Qr Code sous forme d'un String (wifi,lien,text...)
 *
 */
data class Historique(
    val logo:Int = R.drawable.ic_text,
    val imageQR: Bitmap,
    val type: TypeHistorique, //Utilissation de l'Enum class TypeHistorique
    val contenu: String
)