package com.example.benztrack

import DatabaseApp
import android.os.Bundle
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
         lineChart=view.findViewById(R.id.linechart);


        val database = DatabaseApp(requireContext())
        val nometabellaINT = 123
        val nometabellaSTRING = "123"
        database.createTableInfoVehicle("123")
        val entries = getDataFromDatabase(database, nometabellaSTRING)
        setupLineChart(entries)

        btnAdd.setOnClickListener {
            val costoString = costoBollo.text.toString()
            if (costoString.isNotEmpty()) {
                val costoInt = Integer.parseInt(costoString)
                database.insertValueforCar(nometabellaINT, "bollo", nometabellaSTRING, costoInt)
                val newEntries = getDataFromDatabase(database, nometabellaSTRING)
                lineChart.visibility = View.VISIBLE
                updateLineChart(newEntries)
            } else {
                Toast.makeText(requireContext(), "non è stato inserito nulla ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupLineChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "BOLLO")
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun getDataFromDatabase(database: DatabaseApp, tableName: String): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        val dataFromDatabase = database.getDataColumn("bollo",tableName)

        // Itera sui dati ottenuti dal database e crea oggetti Entry
        for ((index, rowData) in dataFromDatabase.withIndex()) {
            val floatValue = rowData.toFloatOrNull() ?: 0f // Converti il valore in float, se possibile
            entries.add(Entry(index.toFloat(), floatValue))
        }
        return entries
    }
    private fun updateLineChart(entries: List<Entry>) {
        val dataSet = LineDataSet(entries, "BOLLO")
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}