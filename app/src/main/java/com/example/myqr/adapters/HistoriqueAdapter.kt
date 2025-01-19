package com.example.myqr.adapters

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

class HistoriqueAdapter(val historiques: List<Historique>,
     val supportFragmentmanager:FragmentManager
    ) : RecyclerView.Adapter<HistoriqueAdapter.HistoriqueViewHolder>() {

    class HistoriqueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageQR: ImageView = itemView.findViewById(R.id.imageQR)
        val typeHistorique: TextView = itemView.findViewById(R.id.typeHistorique)
        val contenu: TextView = itemView.findViewById(R.id.contenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriqueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_historique, parent, false)
        return HistoriqueViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoriqueViewHolder, position: Int) {
        val historique = historiques[position]
        if(historique.type == TypeHistorique.GENERER){
            holder.typeHistorique.text = "Code Generer"
        }else{
            holder.typeHistorique.text = "Code Scanner"
        }

        holder.imageQR.setImageBitmap(historique.imageQR)
        holder.contenu.text = historique.contenu
        holder.itemView.setOnClickListener {
            val bottomSheet = GenerateResultBottomSheetFragment()
            val args = Bundle()
            args.putString("SCANNED_RESULT", historique.contenu)
            bottomSheet.arguments = args
            bottomSheet.show(supportFragmentmanager, "GenerateResultBottomSheetFragment")
        }
    }

    override fun getItemCount(): Int = historiques.size
}