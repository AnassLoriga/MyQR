package com.example.myqr.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myqr.Data.Historique
import com.example.myqr.Data.TypeHistorique
import com.example.myqr.Function.setupSelection
import com.example.myqr.R
import com.example.myqr.Service.HistoriqueService
import com.example.myqr.adapters.HistoriqueAdapter

/**
 * Fragment de dialogue permettant d'afficher l'historique des QR codes générés et scannés.
 */
class historiqueFragment : DialogFragment() {

    // Liste filtrée pour l'affichage dans le RecyclerView
    val listFiltrer = mutableListOf<Historique>()

    // Liste complète des historiques
    lateinit var list: MutableList<Historique>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate la mise en page du fragment d'historique
        return inflater.inflate(R.layout.historique_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        val AllBtn: TextView = view.findViewById(R.id.all)
        val GenererBtn: TextView = view.findViewById(R.id.generer)
        val ScannerBtn: TextView = view.findViewById(R.id.scanner)

        // Configuration du RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = HistoriqueAdapter(listFiltrer, parentFragmentManager)

        // Récupération de l'ensemble des historiques
        list = HistoriqueService.findAll().toMutableList()
        listFiltrer.addAll(list)

        // Liste des boutons pour la gestion de la sélection
        val listBtn = listOf(AllBtn, GenererBtn, ScannerBtn)

        // Bouton pour afficher tous les historiques
        AllBtn.setOnClickListener {
            listFiltrer.clear()
            listFiltrer.addAll(list)
            setupSelection(0, listBtn) // Met en surbrillance le bouton sélectionné
            adapter.notifyDataSetChanged() // Met à jour l'affichage
        }

        // Bouton pour filtrer uniquement les QR codes générés
        GenererBtn.setOnClickListener {
            listFiltrer.clear()
            listFiltrer.addAll(list.filter { it.type == TypeHistorique.GENERER })
            setupSelection(1, listBtn)
            adapter.notifyDataSetChanged()
        }

        // Bouton pour filtrer uniquement les QR codes scannés
        ScannerBtn.setOnClickListener {
            listFiltrer.clear()
            listFiltrer.addAll(list.filter { it.type == TypeHistorique.SCANNER })
            setupSelection(2, listBtn)
            adapter.notifyDataSetChanged()
        }

        // Association de l'adapter au RecyclerView
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    /**
     * Configure la taille et l'apparence du dialogue lors de son affichage.
     */
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, // Largeur : pleine largeur
                ViewGroup.LayoutParams.WRAP_CONTENT  // Hauteur : ajustée au contenu
            )
            setGravity(android.view.Gravity.CENTER) // Centrage du dialogue
            setBackgroundDrawableResource(android.R.color.transparent) // Fond transparent
        }
    }
}
