package com.example.benztrack

import DatabaseApp
import android.R.id.message
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter


class HomeFragment : Fragment() {

    lateinit var pieChart: PieChart


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vehicleSpinner: Spinner = view.findViewById(R.id.veichleSpinner)



        val database = DatabaseApp(requireContext())
        populateVehicle(vehicleSpinner, database)
        pieChart = view.findViewById(R.id.pieChart)

        // Imposta il grafico inizialmente con valori a 0
        setupInitialGraph()
    }

    private fun populateVehicle(spinner: Spinner, database: DatabaseApp) {
        val lista: MutableList<String> = mutableListOf()
        lista.add(0, "Select vehicle")
        lista.add(1, "Lucerne")
        lista.add(2, "Buick")

        val dataFromTable = database.getDataColumnString("model", "CarsTable")

        // Aggiungi i dati alla lista
        lista.addAll(dataFromTable)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            lista
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Azioni da eseguire quando un elemento viene selezionato
                val selectedVehicle = parent?.getItemAtPosition(position).toString()

                if (position > 0) {
                    // Chiama la funzione Grafic solo quando un veicolo è selezionato (non il placeholder)
                    (activity as MainActivity).setSelectedVehicle(selectedVehicle)
                    updateGraphWithDatabaseValues(database)
                }

                // Esempio di azione: visualizzare il veicolo selezionato
                Toast.makeText(
                    requireContext(),
                    "Veicolo selezionato: $selectedVehicle",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Azioni da eseguire quando nessun elemento è selezionato
            }
        }
    }



    private fun setupInitialGraph() {
        pieChart.setUsePercentValues(false)

        // Rimuovi la descrizione
        pieChart.description.isEnabled = false
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // Disabling touch for the pie chart
        pieChart.setTouchEnabled(false)

        // Setting hole and hole color for pie chart
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.WHITE)

        // Setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // Setting hole radius
        pieChart.holeRadius = 15f
        pieChart.transparentCircleRadius = 15f

        pieChart.setDrawCenterText(true)

        // Enabling the legend for pie chart
        pieChart.legend.isEnabled = true
        pieChart.setEntryLabelColor(Color.TRANSPARENT)
        pieChart.setEntryLabelTextSize(12f)
        pieChart.legend.formSize=15f
        pieChart.legend.textSize = 15f

        // Creating array list and adding initial data to it (0 values)
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(10f, "Bollo"))
        entries.add(PieEntry(10f, "Insurance"))
        entries.add(PieEntry(10f, "Fuel"))
        entries.add(PieEntry(10f, "Maintenance"))

        // Setting pie data set
        val dataSet = PieDataSet(entries, "")

        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // Adding colors to the data set
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.colorLightBlue))
        colors.add(resources.getColor(R.color.colorRed))
        colors.add(resources.getColor(R.color.colorLightGreen))
        colors.add(resources.getColor(R.color.colorDutchWhite))
        dataSet.colors = colors

        // Setting pie data
        val data = PieData(dataSet)
        // Imposta il valore del testo su null per rimuovere la scritta sugli spicchi
        data.setDrawValues(true)
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        // Undo all highlights
        pieChart.setHighlightPerTapEnabled(false)

        // Refreshing the chart
        pieChart.invalidate()
    }

    private fun updateGraphWithDatabaseValues(database: DatabaseApp) {
        val entries: ArrayList<PieEntry> = ArrayList()

        // Fetching the data from the database
        val bollo = database.getSumColumn("bollo", "t123").toFloat()
        val assi = database.getSumColumn("assicurazione", "t123").toFloat()
        val benz = database.getSumColumn("benzina", "t123").toFloat()
        val manut = database.getSumColumn("manutenzione", "t123").toFloat()

        // Log the values
        Log.d("HomeFragment", "Bollo: $bollo, Insurance: $assi, Fuel: $benz, yMaintenance: $manut")

        // Adding entries, initializing to 0 if needed
        entries.add(PieEntry(if (bollo == 0f) 0.01f else bollo, "Bollo"))
        entries.add(PieEntry(if (assi == 0f) 0.01f else assi, "Insurance"))
        entries.add(PieEntry(if (benz == 0f) 0.01f else benz, "Fuel"))
        entries.add(PieEntry(if (benz == 0f) 0.01f else manut, "Maintenance"))

        // Setting pie data set
        val dataSet = PieDataSet(entries, "")
        pieChart.legend.formSize=15f
        pieChart.legend.textSize = 15f

        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        // Adding colors to the data set
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.colorLightBlue))
        colors.add(resources.getColor(R.color.colorRed))
        colors.add(resources.getColor(R.color.colorLightGreen))
        colors.add(resources.getColor(R.color.colorDutchWhite))
        dataSet.colors = colors

        // Setting pie data
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter(pieChart)) // This will format values as percentages
        // Imposta il valore del testo su null per rimuovere

        // This will format values as percentages
        // Imposta il valore del testo su null per rimuovere la scritta sugli spicchi

        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data

        // Undo all highlights
        pieChart.setHighlightPerTapEnabled(false)

        // Refreshing the chart
        pieChart.invalidate()
    }

}
