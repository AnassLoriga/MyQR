package com.example.myqr.Data

import android.graphics.Bitmap

data class Historique(
    val imageQR: Bitmap,
    val type: TypeHistorique,
    val contenu: String
)