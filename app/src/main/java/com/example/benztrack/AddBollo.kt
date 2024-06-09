package com.example.benztrack

import DatabaseApp
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

        val selectedVehicleHome = arguments?.getString("data")
        val txtVehicle = view.findViewById<TextView>(R.id.txtVehicle)
        if (selectedVehicleHome == null) {
            Toast.makeText(requireContext(), "Nessun veicolo selezionato", Toast.LENGTH_SHORT).show()
            return
        }

        txtVehicle.text = "Veicolo selezionato: $selectedVehicleHome"

        val costoBollo = view.findViewById<EditText>(R.id.CostoBollo)
        val costoAssicurazione = view.findViewById<EditText>(R.id.CostoAssicurazione)
        val btnAdd = view.findViewById<Button>(R.id.Add)
        lineChart = view.findViewById(R.id.linechart)
        lineChart.description.isEnabled = false

        val database = DatabaseApp(requireContext())
        val tableName: String = selectedVehicleHome.toString()




            // Recupera i dati dal database e aggiorna il grafico quando il fragment viene creato
            val initialEntriesB = getDataFromDatabase(database, tableName, "bollo")
            val initialEntriesA = getDataFromDatabase(database, tableName, "assicurazione")
            updateLineChart(initialEntriesB, initialEntriesA)


            btnAdd.setOnClickListener {
                val costoString = costoBollo.text.toString()
                val costoStringAssi = costoAssicurazione.text.toString()

                if (costoString.isNotEmpty()) {
                    val costoDouble = costoString.toDouble()
                    database.insertValueforCar("bollo", tableName, costoDouble)
                    val newEntriesB = getDataFromDatabase(database, tableName, "bollo")
                    val newEntriesA = getDataFromDatabase(database, tableName, "assicurazione")
                    updateLineChart(newEntriesB, newEntriesA)
                }

                if (costoStringAssi.isNotEmpty()) {
                    val costoDouble = costoStringAssi.toDouble()
                    database.insertValueforCar("assicurazione", tableName, costoDouble)
                    val newEntriesA = getDataFromDatabase(database, tableName, "assicurazione")
                    val newEntriesB = getDataFromDatabase(database, tableName, "bollo")
                    updateLineChart(newEntriesB, newEntriesA)
                }

                if (costoString.isEmpty() && costoStringAssi.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Inserire assicurazione e/o bollo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }

    private fun getDataFromDatabase(
        database: DatabaseApp,
        tableName: String,
        colonna: String
    ): ArrayList<Entry> {
        val entries = ArrayList<Entry>()
        val dataFromDatabase = database.getDataColumnDouble(colonna, tableName)

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
        bolloDataSet.color = resources.getColor(R.color.colorLightBlue)
        bolloDataSet.setCircleColor(resources.getColor(R.color.colorLightBlue))

        val assicurazioneDataSet = LineDataSet(assicurazioneEntries, "Insurance")
        assicurazioneDataSet.color = resources.getColor(R.color.colorRed)
        assicurazioneDataSet.setCircleColor(resources.getColor(R.color.colorRed))

        val lineData = LineData(bolloDataSet, assicurazioneDataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }
}