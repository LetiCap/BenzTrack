package com.example.benztrack

import DatabaseApp
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
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
import com.github.mikephil.charting.utils.MPPointF

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
        populateVehicle( vehicleSpinner, database)
        pieChart = view.findViewById(R.id.pieChart)

        // on below line we are setting user percent value,
        // setting description as enabled and offset for pie chart
        this.Grafic(database)




    }

    private fun populateVehicle( spinner: Spinner,  database: DatabaseApp) {
        var scritta = ""


       val lista : MutableList<String> =  mutableListOf()
        lista.add(0, "Seleziona un veicolo")
        val dataFromTable = database.getAllData("CarsTable")

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
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Azioni da eseguire quando un elemento viene selezionato
                val selectedVehicle = parent?.getItemAtPosition(position).toString()
                // Esempio di azione: visualizzare il veicolo selezionato
                Toast.makeText(requireContext(), "Veicolo selezionato: $selectedVehicle", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Azioni da eseguire quando nessun elemento è selezionato
            }
        }



    }
    private fun Grafic(database: DatabaseApp) {

        pieChart.setUsePercentValues(false)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        pieChart.setTouchEnabled(false)


        // on below line we are setting hole
        // and hole color for pie chart
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        // on  below line we are setting hole radius
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        // on below line we are setting center text
        pieChart.setDrawCenterText(true)



        // on below line we are disabling our legend for pie chart
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        val bollo= database.getSumColumn("bollo", "123").toFloat()
        val assi=database.getSumColumn("assicurazione", "123").toFloat()
        val benz= database.getSumColumn("benzina", "123").toFloat()
        entries.add(PieEntry(bollo,"Bollo"))
        entries.add(PieEntry(assi,"assicurazione"))
        entries.add(PieEntry(benz,"benzina"))

        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Mobile OS")

        // on below line we are setting icons.
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.yellow))
        colors.add(resources.getColor(R.color.red))

        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        // undo all highlights
        pieChart.setHighlightPerTapEnabled(false)

        // loading chart
        pieChart.invalidate()
    }







}
