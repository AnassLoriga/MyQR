package com.example.myqr.Service

import android.widget.Toast
import com.example.myqr.Data.Historique

object HistoriqueService{
    val listHistorique = mutableListOf<Historique>()
    fun addHistorique(H:Historique):Boolean{
        if(!listHistorique.contains(H)) {
            listHistorique.add(H)
            return true
        }
        return false
    }
    fun findAll():MutableList<Historique>{
        return listHistorique.toMutableList()
    }
}