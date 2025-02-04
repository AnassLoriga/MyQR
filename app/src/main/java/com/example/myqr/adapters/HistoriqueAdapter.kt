package com.example.myqr.adapters

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myqr.Data.Historique
import com.example.myqr.Data.TypeHistorique
import com.example.myqr.R
import com.example.myqr.fragments.GenerateResultBottomSheetFragment
import java.io.ByteArrayOutputStream

/**
 * Adapter pour gérer l'affichage des éléments de l'historique dans un RecyclerView.
 * Il permet d'afficher les QR codes générés ou scannés avec leurs détails associés.
 *
 * @param historiques Liste des éléments de l'historique à afficher.
 * @param supportFragmentmanager Gestionnaire de fragments pour afficher les BottomSheet.
 */
class HistoriqueAdapter(
    val historiques: List<Historique>,
    val supportFragmentmanager: FragmentManager
) : RecyclerView.Adapter<HistoriqueAdapter.HistoriqueViewHolder>() {

    /**
     * ViewHolder pour représenter chaque élément de l'historique.
     *
     * @param itemView Vue de l'élément de la liste.
     */
    class HistoriqueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageQR: ImageView = itemView.findViewById(R.id.imageQR)
        val typeHistorique: TextView = itemView.findViewById(R.id.typeHistorique)
        val contenu: TextView = itemView.findViewById(R.id.contenu)
    }

    /**
     * Crée et retourne un ViewHolder pour un nouvel élément de la liste.
     *
     * @param parent Le ViewGroup parent dans lequel la vue sera attachée.
     * @param viewType Le type de vue de l'élément.
     * @return Un nouvel objet HistoriqueViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriqueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historique, parent, false)
        return HistoriqueViewHolder(view)
    }

    /**
     * Lie les données d'un élément de l'historique à la vue correspondante.
     *
     * @param holder Le ViewHolder qui doit être mis à jour.
     * @param position La position de l'élément dans la liste.
     */
    override fun onBindViewHolder(holder: HistoriqueViewHolder, position: Int) {
        val historique = historiques[position]
        if (historique.type == TypeHistorique.GENERER) {
            holder.typeHistorique.text = "Code Generer"
        } else {
            holder.typeHistorique.text = "Code Scanner"
        }

        val bitmap = historique.imageQR
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()

        holder.imageQR.setImageResource(historique.logo)
        holder.contenu.text = historique.contenu

        holder.itemView.setOnClickListener {
            val bottomSheet = GenerateResultBottomSheetFragment()
            val args = Bundle()
            args.putString("SCANNED_RESULT", historique.contenu)
            args.putByteArray("bitmap", byteArray)
            bottomSheet.arguments = args
            bottomSheet.show(supportFragmentmanager, "GenerateResultBottomSheetFragment")
        }
    }

    /**
     * Retourne le nombre total d'éléments dans la liste de l'historique.
     *
     * @return Le nombre d'éléments.
     */
    override fun getItemCount(): Int = historiques.size
}
