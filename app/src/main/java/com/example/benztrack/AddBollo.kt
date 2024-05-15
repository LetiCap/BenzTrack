package com.example.benztrack

import DatabaseApp
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter


class AddBollo : Fragment() {
    lateinit var lineChart: LineChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bollo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val costoBollo = view.findViewById<EditText>(R.id.CostoBollo)
        val btnAdd = view.findViewById<Button>(R.id.Add)
        lineChart = view.findViewById(R.id.linechart)

        val database = DatabaseApp(requireContext())
        val tableName = "t123"
        database.createTableInfoVehicle(tableName)
        val datidaldatabase=getDataFromDatabase(database, tableName)
        updateLineChart(datidaldatabase)


        btnAdd.setOnClickListener {
            val costoString = costoBollo.text.toString()
            if (costoString.isNotEmpty()) {
                val costoInt = costoString.toInt()
                database.insertValueforCar("bollo", tableName, costoInt)
                val newEntries = getDataFromDatabase(database, tableName)
                updateLineChart(newEntries)

            } else {
                Toast.makeText(requireContext(), "Inserire il costo del bollo", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getDataFromDatabase(
        database: DatabaseApp,
        tableName: String
    ): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        val dataFromDatabase = database.getDataColumn("bollo", tableName)

        // Itera sui dati ottenuti dal database e crea oggetti Entry
        for ((index, value) in dataFromDatabase.withIndex()) {
            val entry = Entry(index.toFloat(), value.toFloat())
            entries.add(entry)
        }

        return entries
    }



    private fun updateLineChart(entries: ArrayList<Entry>) {
        val dataSet = LineDataSet(entries, "BOLLO")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configura l'asse x per visualizzare le date come etichette personalizzate

        lineChart.invalidate()
    }


}
