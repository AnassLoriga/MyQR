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

class historiqueFragment : DialogFragment() {
    val listFiltrer=mutableListOf<Historique>()
    lateinit var list:MutableList<Historique>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.historique_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycleView)
        val AllBtn: TextView = view.findViewById(R.id.all)
        val GenererBtn: TextView = view.findViewById(R.id.generer)
        val ScannerBtn: TextView = view.findViewById(R.id.scanner)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val adapter = HistoriqueAdapter(listFiltrer,parentFragmentManager)
        list = HistoriqueService.findAll().toMutableList()
        listFiltrer.addAll(list)
        val listBtn= listOf(AllBtn,GenererBtn,ScannerBtn)
        AllBtn.setOnClickListener {
            listFiltrer.clear()
            listFiltrer.addAll(list)
            setupSelection(0,listBtn)
            adapter.notifyDataSetChanged()
        }
        GenererBtn.setOnClickListener {
            listFiltrer.clear()
            listFiltrer.addAll(list.filter {it.type ==TypeHistorique.GENERER})
            setupSelection(1,listBtn)
            adapter.notifyDataSetChanged()
        }
            ScannerBtn.setOnClickListener {
                listFiltrer.clear()
                listFiltrer.addAll(list.filter {it.type ==TypeHistorique.SCANNER})
                setupSelection(2,listBtn)
                adapter.notifyDataSetChanged()
            }
        recyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

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
