package com.example.greenify

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ImpactFragment : Fragment() {

    private lateinit var carTextView: TextView
    private lateinit var treeTextView: TextView
    private lateinit var aeroplaneTextView: TextView
    private lateinit var wasteTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_impact, container, false)

        carTextView = view.findViewById(R.id.car)
        treeTextView = view.findViewById(R.id.tree)
        aeroplaneTextView = view.findViewById(R.id.aeroplane)
        wasteTextView = view.findViewById(R.id.waste)

        // Firestore'dan verileri çek ve TextView'leri güncelle
        updateImpactValues()

        return view
    }

    private fun updateImpactValues() {
        val db = FirebaseFirestore.getInstance()
        db.collection("impact").limit(1).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val impactDoc = querySnapshot.documents[0]
                    val car = impactDoc.getLong("car") ?: 0
                    val tree = impactDoc.getLong("tree") ?: 0
                    val aeroplane = impactDoc.getLong("aeroplane") ?: 0
                    val waste = impactDoc.getLong("waste") ?: 0
                    carTextView.text = "Ortalama bir binek aracın $car km yol yapması"
                    treeTextView.text = "$tree yetişkin ağacın yıllık CO2 emme kapasitesi"
                    aeroplaneTextView.text = "1 kişinin $aeroplane dakikalık uçak yolculuğu"
                    wasteTextView.text = "$waste kg atık geri dönüştürüldü"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error fetching impact values: ", exception)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }
}
