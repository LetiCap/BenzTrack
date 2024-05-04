import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.benztrack.CarExpandableListAdapter
import com.example.benztrack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class AddingCarFragment : Fragment() {
    private var selectedAnno: String = ""
    private var selectedType: String = ""
    private var selectedMakes: String = ""
    private var userClickedSpinner: Boolean = false

    private var AnnoList: MutableList<String> = mutableListOf()
    private var MarchioList: MutableList<String> = mutableListOf()
    private var TipoList: MutableList<String> = mutableListOf()

    private lateinit var expandableListView: ExpandableListView
    private lateinit var carExpandableListAdapter: CarExpandableListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_addingcar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val TipoSpinner: Spinner = view.findViewById(R.id.Tipo)
        val AnnoSpinner: Spinner = view.findViewById(R.id.Anno)
        val MarchioSpinner: Spinner = view.findViewById(R.id.Marchio)
        val btn = view.findViewById<Button>(R.id.btnAdd)
        expandableListView = view.findViewById(R.id.expandableListView)
        expandableListView.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.white))

        lifecycleScope.launch {
            populateSpinner("makes", MarchioSpinner, MarchioList)
            delay(200)
            populateSpinner("years", AnnoSpinner, AnnoList)
            delay(200)
            populateSpinner("types", TipoSpinner, TipoList)
        }

        MarchioSpinner.onItemSelectedListener = createItemSelectedListener(MarchioList, "Marchio")
        TipoSpinner.onItemSelectedListener = createItemSelectedListener(TipoList, "Tipo")
        AnnoSpinner.onItemSelectedListener = createItemSelectedListener(AnnoList, "Anno")

        btn.setOnClickListener {
            if (!userClickedSpinner) {
                Toast.makeText(requireContext(), "Seleziona un anno", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TipoSpinner.selectedItemPosition == 0 || MarchioSpinner.selectedItemPosition == 0 || AnnoSpinner.selectedItemPosition == 0) {
                Toast.makeText(
                    requireContext(),
                    "Seleziona un parametro valido per ogni sezione",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                fetchData(selectedType, selectedAnno, selectedMakes)
            }
        }
    }

    private suspend fun populateSpinner(item: String, spinner: Spinner, lista: MutableList<String>) {
        var scritta = ""
        when (item) {
            "years" -> scritta = "Anno"
            "types" -> scritta = "Tipo"
            "makes" -> scritta = "Marchio"
        }

        lista.add(0, "Seleziona un $scritta")
        val requestUrl = "https://car-data.p.rapidapi.com/cars/$item"

        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(requestUrl)
                .get()
                .addHeader(
                    "X-RapidAPI-Key",
                    "3d2a9c66e1msh98394268003597ep10489bjsn42dc7dfe3373"
                )
                .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
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

                withContext(Dispatchers.Main) {
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        lista
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = adapter
                }
            } else {
                throw IOException("Errore nella richiesta: ${response.code}")
            }
        } catch (e: IOException) {
            Log.e("AddingCarFragment", "Errore di connessione: ${e.message}")
        }
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
                    .addHeader(
                        "X-RapidAPI-Key",
                        "3d2a9c66e1msh98394268003597ep10489bjsn42dc7dfe3373"
                    )
                    .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
                    .build()

                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    Log.d("AddingCarFragment", "Risposta grezza dall'API: $responseBody")
                    val carData = parseResponse(responseBody)
                    updateListView(carData)
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

    private fun parseResponse(responseBody: String?): MutableList<Pair<String, MutableList<Pair<String, String>>>> {
        val carList = mutableListOf<Pair<String, MutableList<Pair<String, String>>>>()

        responseBody?.let {
            val vehicles = it.removeSurrounding("[", "]").split("},")
            vehicles.forEach { vehicle ->
                val vehicleDetails = vehicle.split(",")
                val vehicleId = vehicleDetails.find { it.startsWith("id:") }?.substringAfter("id:\"")?.trim(' ', '"') ?: ""
                val vehicleModel = vehicleDetails.find { it.startsWith("model:") }?.substringAfter("model:\"")?.trim(' ', '"') ?: ""
                val vehicleType = vehicleDetails.find { it.startsWith("type:") }?.substringAfter("type:\"")?.trim(' ', '"') ?: ""
                val vehicleMake = vehicleDetails.find { it.startsWith("make:") }?.substringAfter("make:\"")?.trim(' ', '"') ?: ""
                val vehicleYear = vehicleDetails.find { it.startsWith("year:") }?.substringAfter("year:\"")?.trim(' ', '"') ?: ""

                val details = mutableListOf<Pair<String, String>>()
                details.add(Pair("model", vehicleModel))
                details.add(Pair("type", vehicleType))
                details.add(Pair("year", vehicleYear))
                details.add(Pair("make", vehicleMake))

                carList.add(Pair(vehicleId, details))
            }
        }

        return carList
    }


    private fun updateListView(carData: MutableList<Pair<String, MutableList<Pair<String, String>>>>) {
        carExpandableListAdapter = CarExpandableListAdapter(requireContext(), carData)
        expandableListView.setAdapter(carExpandableListAdapter)
        expandableListView.visibility = View.VISIBLE

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val vehicleDetails = carData[groupPosition].second[childPosition]
            val message = "Dettagli del veicolo:\nModello: ${vehicleDetails.first}\nTipo: ${vehicleDetails.second}"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            true
        }
    }
}
