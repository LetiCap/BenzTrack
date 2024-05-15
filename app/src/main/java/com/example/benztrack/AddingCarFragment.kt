import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope

import com.example.benztrack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

class AddingCarFragment : Fragment() {
    private var selectedAnno: String = ""
    private var selectedType: String = ""
    private var selectedMakes: String = ""
    private var userClickedSpinner: Boolean = false

    private var AnnoList: MutableList<String> = mutableListOf()
    private var MarchioList: MutableList<String> = mutableListOf()
    private var TipoList: MutableList<String> = mutableListOf()
    private var vehicleModel: MutableList<String> = mutableListOf()

    private lateinit var expandableListView: ExpandableListView
    private lateinit var carExpandableListAdapter: CarExpandableListAdapter
    private lateinit var btnAdd: Button
    private lateinit var CO2: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_addingcar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val TipoSpinner:Spinner = view.findViewById(R.id.Tipo)
        val AnnoSpinner: Spinner = view.findViewById(R.id.Anno)
        val MarchioSpinner: Spinner = view.findViewById(R.id.Marchio)
        CO2 = view.findViewById<EditText>(R.id.ConsumoCO2)
        val database = DatabaseApp(requireContext())
        btnAdd = view.findViewById(R.id.btnAdd)
        expandableListView = view.findViewById(R.id.expandableListView)
        expandableListView.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        lifecycleScope.launch {
            delay(500)
            populateSpinner("makes", MarchioSpinner, MarchioList, database)
            delay(500)
            populateSpinner("years", AnnoSpinner, AnnoList,database)
            delay(500)
            populateSpinner("types", TipoSpinner, TipoList, database)
        }

        MarchioSpinner.onItemSelectedListener = createItemSelectedListener(MarchioList, "Marchio")
        TipoSpinner.onItemSelectedListener = createItemSelectedListener(TipoList, "Tipo")
        AnnoSpinner.onItemSelectedListener = createItemSelectedListener(AnnoList, "Anno")

        btnAdd.setOnClickListener {

            if (!userClickedSpinner) {
                Toast.makeText(requireContext(), "Seleziona un anno", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TipoSpinner.selectedItemPosition == 0 || MarchioSpinner.selectedItemPosition == 0 || AnnoSpinner.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Seleziona un parametro valido per ogni sezione", Toast.LENGTH_LONG).show()
            } else {
                fetchData(selectedType, selectedAnno, selectedMakes)
            }
        }
    }
/*
    val tv = view.findViewById<TextView>(R.id.text_home)
    val tv1 = view.findViewById<TextView>(R.id.textView2)
    val tv2 = view.findViewById<TextView>(R.id.textView)

    // Inizializzazione del database
    val database = DatabaseApp(requireContext())

    // Recupera tutti i dati dalla tabella "TypesTable"
    val datatype = database.getAllData("TypesTable")
    val datamake = database.getAllData("MakesTable")
    val datayear = database.getAllData("YearsTable")

    // Costruisci una stringa con i dati ottenuti


    var tabValue = ""
    for (data in datatype) {
        tabValue += "$data"
    }
    var tab = ""
    for (data in datamake) {
        tab += "$data"
    }
    var tabV = ""
    for (data in datayear) {
        tabV += "$data"
    }

    // Imposta la stringa nel TextView
    tv.text = tabValue
    tv1.text = tab
    tv2.text = tabV
    */

    private suspend fun populateSpinner(item: String, spinner: Spinner, lista: MutableList<String>, database: DatabaseApp) {
        var scritta = ""
        var tabella=""
        when (item) {
            "years" -> { scritta = "Anno" ; tabella ="YearsTable" }
            "types" -> {scritta = "Tipo"; tabella ="TypesTable" }
            "makes" -> {scritta = "Marchio"; tabella ="MakesTable" }
        }

        lista.add(0, "Seleziona un $scritta")


        val dataFromTable = database.getAllData(tabella)

        // Aggiungi i dati alla lista
        lista.addAll(dataFromTable)



/*
        val requestUrl = "https://car-data.p.rapidapi.com/cars/$item"

        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(requestUrl)
                .get()
                .addHeader("X-RapidAPI-Key", "0dc8f0efbdmsha7a5bc2f50d8e7dp1a3ad3jsn088f99490027")
                .build()
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                val responseArray = responseBody
                    ?.removeSurrounding("[", "]")
                    ?.replace("\"", "")
                    ?.split(",")
                    ?.toTypedArray()
                responseArray?.let { lista.addAll(it) }
*/
                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        lista
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
     /*       } else {
                throw IOException("Errore nella richiesta: ${response.code}")
            }
        } catch (e: IOException) {
            Log.e("AddingCarFragment", "Errore di connessione: ${e.message}")
        }*/
    }

    private fun createItemSelectedListener(
        ListaPassata: MutableList<String>,
        MessaggioPerMancatoInserimento: String
    ): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (ListaPassata.isNotEmpty() && position >= 0 && position < ListaPassata.size) {
                    val selectedOption = ListaPassata[position]
                    when (MessaggioPerMancatoInserimento) {
                        "Tipo" -> {
                            selectedType = selectedOption
                            Log.d("AddingCarFragment", "Tipo selezionato: $selectedOption")
                        }
                        "Anno" -> {
                            selectedAnno = selectedOption
                            Log.d("AddingCarFragment", "Anno selezionato: $selectedOption")
                        }
                        "Marchio" -> {
                            selectedMakes = selectedOption
                            Log.d("AddingCarFragment", "Marchio selezionato: $selectedOption")
                        }
                    }
                    userClickedSpinner = true
                } else {
                    Log.e("AddingCarFragment", "Lista vuota o indice non valido")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun fetchData(
        tipo: String?,
        anno: String?,
        marca: String?,
    ) {
        val requestUrl =
            "https://car-data.p.rapidapi.com/cars?limit=50&page=0&year=$anno&make=$marca&type=$tipo"


        lifecycleScope.launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(requestUrl)
                    .get()
                    .addHeader("X-RapidAPI-Key", "0dc8f0efbdmsha7a5bc2f50d8e7dp1a3ad3jsn088f99490027")
                    .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("AddingCarFragment", "Risposta grezza dall'API: $responseBody")
                    val carData = parseResponse(responseBody)
                    if (carData.isEmpty()){
                        Toast.makeText(requireContext(), "niente è stato trovato", Toast.LENGTH_LONG)
                            .show()
                    }else{
                    updateListView(carData)}
                } else {
                    throw IOException("Errore nella richiesta: ${response.code}")
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Errore di connessione", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun parseResponse(responseBody: String?): List<List<Pair<String, String>>> {
        val vehicleList = mutableListOf<List<Pair<String, String>>>()

        responseBody?.let {
            val vehiclesJsonArray = JSONArray(it)
            for (i in 0 until vehiclesJsonArray.length()) {
                val vehicleJsonObject = vehiclesJsonArray.getJSONObject(i)
                val details = mutableListOf<Pair<String, String>>()

                val model = vehicleJsonObject.getString("model")
                // Ottieni l'ID del veicolo
                vehicleModel.add(model.toString())


                details.add(Pair("Modello", model))
                details.add(Pair("Tipo", vehicleJsonObject.getString("type")))
                details.add(Pair("Anno", vehicleJsonObject.getString("year")))
                details.add(Pair("Marchio", vehicleJsonObject.getString("make")))
                vehicleList.add(details)
            }
        }

        return vehicleList
    }

    private fun updateListView(vehicleDetailsList: List<List<Pair<String, String>>>) {
        val vehicleDescriptions = vehicleDetailsList.mapIndexed { index, details ->
            val description = StringBuilder("Veicolo ${index + 1}\n")
            description.toString()
        }
        val Co2String = CO2.text.toString()
        val CO2 = Co2String.toInt()
        carExpandableListAdapter = CarExpandableListAdapter(requireContext(), vehicleDescriptions, vehicleDetailsList, vehicleModel,CO2)
        expandableListView.setAdapter(carExpandableListAdapter)
        btnAdd.visibility = View.INVISIBLE
        expandableListView.visibility = View.VISIBLE

    }

}
