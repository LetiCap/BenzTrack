package com.example.benztrack


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class AddManutenzione : Fragment() {
    private lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_manutenzione, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val costoAssicurazione = view.findViewById<EditText>(R.id.CostoManutenzione)
        val btnAdd = view.findViewById<Button>(R.id.addMaintenance)
        val selectedVehicleHome = arguments?.getString("data")
        val txtVehicle = view.findViewById<TextView>(R.id.txtVehicle)
        if (selectedVehicleHome == null) {
            Toast.makeText(requireContext(), "Nessun veicolo selezionato", Toast.LENGTH_SHORT).show()
            return
        }

        txtVehicle.text = "Veicolo selezionato: $selectedVehicleHome"

        lineChart = view.findViewById(R.id.linechart)
        lineChart.description.isEnabled = false

        val database = DatabaseApp(requireContext())
        val tableName = selectedVehicleHome.toString()
        val datidaldatabase=getDataFromDatabase(database, tableName)
        updateLineChart(datidaldatabase)


        btnAdd.setOnClickListener {
            val costoString = costoAssicurazione.text.toString()
            if (costoString.isNotEmpty()) {
                val costoDouble = costoString.toDouble()
                database.insertValueforCar("manutenzione", tableName, costoDouble)
                val newEntries = getDataFromDatabase(database, tableName)
                updateLineChart(newEntries)

            } else {
                Toast.makeText(requireContext(), "Inserire il costo dell'assicurazione", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getDataFromDatabase(
        database: DatabaseApp,
        tableName: String
    ): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        val dataFromDatabase = database.getDataColumnDouble("manutenzione", tableName)

        // Itera sui dati ottenuti dal database e crea oggetti Entry
        for ((index, value) in dataFromDatabase.withIndex()) {
            val entry = Entry(index.toFloat(), value.toFloat())
            entries.add(entry)
        }

        return entries
    }

    private fun updateLineChart(entries: ArrayList<Entry>) {
        val dataSet = LineDataSet(entries, "Maintenance")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configura l'asse x per visualizzare le date come etichette personalizzate

        lineChart.invalidate()
    }
}