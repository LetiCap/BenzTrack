package com.example.benztrack



import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class FuelGraphs : AppCompatActivity() {

    private lateinit var lineChart1: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var lineChart3: LineChart
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fuel_graphs)






        // attiva pulsate home (freccina) per tornare al fragment precedente l'apertura dell'activity
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        // Gestione degli insets per evitare sovrapposizioni con la barra delle azioni
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom)
            WindowInsetsCompat.Builder(insets)
                .setInsets(WindowInsetsCompat.Type.systemBars(), systemBarsInsets)
                .build()
        }

        lineChart1 = findViewById(R.id.linechart)
        lineChart2 = findViewById(R.id.linechart2)
        lineChart3 = findViewById(R.id.linechart3)
        lineChart1.description.isEnabled = false
        lineChart2.description.isEnabled = false
        lineChart3.description.isEnabled = false
        val database = DatabaseApp(this)

        val tableName = intent.getStringExtra("veicolo selezionato")
        Log.d("addFuel", "$tableName")

        if (tableName != null) {

            val dataFromDatabaseB = getDataFromDatabase(database, tableName, "benzina")
            val dataFromDatabaseC = getDataFromDatabase(database, tableName, "CostoBenzina")
            val dataFromDatabaseD = getDataFromDatabase(database, tableName, "consumoCO2")
            updateLineChart(dataFromDatabaseB,"Money for fuel",lineChart1)
            updateLineChart(dataFromDatabaseC,"Fuel cost per Litre",lineChart2)
            updateLineChart(dataFromDatabaseD,"CO2 consumption",lineChart3)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleBackNavigation()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleBackNavigation() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    private fun updateLineChart(entries: ArrayList<Entry>, description :String, lineChart:LineChart) {
        val dataSet = LineDataSet(entries,description )
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configura l'asse x per visualizzare le date come etichette personalizzate

        lineChart.invalidate()
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

}
