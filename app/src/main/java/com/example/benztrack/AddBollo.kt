package com.example.benztrack

import DatabaseApp
import android.graphics.Color
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
        val costoAssicurazione = view.findViewById<EditText>(R.id.CostoAssicurazione)

        val btnAdd = view.findViewById<Button>(R.id.Add)
        lineChart = view.findViewById(R.id.linechart)
        lineChart.description.isEnabled = false
        //casino

        val database = DatabaseApp(requireContext())
        val tableName = "t123"
        database.createTableInfoVehicle(tableName)
       // val bollodalDatabase=getDataFromDatabase(database, tableName,"bollo")
       // val assicurazionedalDatabase=getDataFromDatabase(database, tableName,"assicurazione")
        //updateLineChart(bollodalDatabase,assicurazionedalDatabase )


        btnAdd.setOnClickListener {
            val costoString = costoBollo.text.toString()
            val costoStringAssi = costoAssicurazione.text.toString()
            if (costoString.isNotEmpty()) {
                val costoDouble = costoString.toDouble()
                database.insertValueforCar("bollo", tableName, costoDouble)
                val newEntriesB = getDataFromDatabase(database, tableName, "bollo")
                val newEntriesA = getDataFromDatabase(database, tableName,"assicurazione")

                updateLineChart(newEntriesB,newEntriesA)

            }
            if (costoStringAssi.isNotEmpty()) {
                val costoDouble = costoStringAssi.toDouble()
                database.insertValueforCar("assicurazione", tableName, costoDouble)
                val newEntriesA = getDataFromDatabase(database, tableName,"assicurazione")
                val newEntriesB = getDataFromDatabase(database, tableName, "bollo")
                updateLineChart(newEntriesB,newEntriesA)

            }
            if(costoString.isEmpty()&&costoStringAssi.isEmpty() ){
                Toast.makeText(requireContext(), "Inserire assicurazione e/o bollo", Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    private fun getDataFromDatabase(
        database: DatabaseApp,
        tableName: String,
        colonna:String
    ): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        val dataFromDatabase = database.getDataColumnDouble(colonna, tableName)

        // Itera sui dati ottenuti dal database e crea oggetti Entry
        for ((index, value) in dataFromDatabase.withIndex()) {
            val entry = Entry(index.toFloat(), value.toFloat())
            entries.add(entry)
        }

        return entries
    }



    private fun updateLineChart(
        bolloEntries: ArrayList<Entry>,
        assicurazioneEntries: ArrayList<Entry>
    ) {
        val bolloDataSet = LineDataSet(bolloEntries, "Bollo")
        bolloDataSet.color = resources.getColor(R.color.colorLightBlue) // Imposta il colore della linea del bollo
        bolloDataSet.setCircleColor(resources.getColor(R.color.colorLightBlue)) // Imposta il colore dei cerchi

        val assicurazioneDataSet = LineDataSet(assicurazioneEntries, "Insurance")
        assicurazioneDataSet.color = resources.getColor(R.color.colorRed) // Imposta il colore della linea dell'assicurazione
        assicurazioneDataSet.setCircleColor(resources.getColor(R.color.colorRed)) // Imposta il colore dei cerchi


        val lineData = LineData(bolloDataSet, assicurazioneDataSet)
        lineChart.data = lineData

        // Configura l'asse x per visualizzare le date come etichette personalizzate

        lineChart.invalidate()
    }


}
