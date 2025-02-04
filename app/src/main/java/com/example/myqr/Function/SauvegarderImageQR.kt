package com.example.myqr.Function

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.example.myqr.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.OutputStream

/**
 * Enregistre un QR code sous forme d'image dans la galerie de l'appareil.
 *
 * @param bitmap L'image du QR code à enregistrer.
 * @param fileName Le nom du fichier sans extension.
 * @param context Le contexte de l'application pour accéder aux ressources système.
 */
fun saveQRCodeToGallery(bitmap: Bitmap, fileName: String, context: Context) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyQR")
    }

    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (uri != null) {
        try {
            val outputStream: OutputStream? = resolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            outputStream?.close()
            Toast.makeText(context, "QR Code enregistré dans la galerie", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de l'enregistrement", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Impossible d'accéder à la galerie", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Masque le clavier virtuel à partir d'une vue donnée.
 *
 * @param context Le contexte de l'application.
 * @param view La vue actuellement focalisée.
 */
fun hideKeyboard(context: Context, view: View) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Met à jour la sélection visuelle d'une liste de boutons de type `TextView`.
 *
 * @param position L'index de l'élément sélectionné dans la liste.
 * @param list La liste des `TextView` à gérer.
 */
fun setupSelection(position: Int, list: List<TextView>) {
    val clickBtn = list[position]
    list.forEach { it.setBackgroundResource(R.drawable.toggle_unselected) }
    clickBtn.setBackgroundResource(R.drawable.toggle_selected)
}

/**
 * Génère un QR code à partir d'un texte et le partage avec l'image correspondante.
 *
 * @param context Le contexte de l'application.
 * @param text Le texte à encoder dans le QR code.
 */
fun shareBitmapAndText(context: Context, text: String) {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
    for (x in 0 until 512) {
        for (y in 0 until 512) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }

    val imageUri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "shared_image.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/SharedImages")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
        }
        uri
    } else {
        val imagesDir = context.externalCacheDir
        val image = File(imagesDir, "shared_image.jpg")
        image.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        Uri.fromFile(image)
    }

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/jpeg"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    context.startActivity(Intent.createChooser(shareIntent, "Partager via"))
}
