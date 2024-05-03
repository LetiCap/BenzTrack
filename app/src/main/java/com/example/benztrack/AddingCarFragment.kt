package com.example.benztrack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class AddingCarFragment : Fragment() {
    private var selectedModel: String = ""
    private var selectedAnno: String = ""
    private var selectedType: String = ""
    private var selectedMakes: String = ""
    private var selectedCar: String = ""


    private var AnnoList: MutableList<String> = mutableListOf()
    private var MarchioList: MutableList<String> = mutableListOf()
    private var ModelloList: MutableList<String> = mutableListOf()
    private var TipoList: MutableList<String> = mutableListOf()
    private var CarList: MutableList<String> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_addingcar, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val outResSpinner:Spinner = view.findViewById(R.id.result)
        val TipoSpinner: Spinner = view.findViewById(R.id.Tipo)
        val AnnoSpinner: Spinner = view.findViewById(R.id.Anno)
        val MarchioSpinner: Spinner = view.findViewById(R.id.Marchio)
        val btn = view.findViewById<Button>(R.id.btnAdd)




        lifecycleScope.launch {
            populateSpinner("makes", MarchioSpinner, MarchioList)
            delay(100)
            populateSpinner("years", AnnoSpinner, AnnoList)
            delay(100)
            populateSpinner("types",TipoSpinner,TipoList)
        }


        //



        MarchioSpinner.onItemSelectedListener = createItemSelectedListener( MarchioList,"Marchio")
        TipoSpinner.onItemSelectedListener = createItemSelectedListener(TipoList,"Tipo")
        //ModelloSpinner.onItemSelectedListener = createItemSelectedListener( ModelloList, "Modello")
        AnnoSpinner.onItemSelectedListener = createItemSelectedListener( AnnoList,"Anno")



        btn.setOnClickListener {
            if (TipoSpinner.selectedItemPosition == 0 || MarchioSpinner.selectedItemPosition ==0 || AnnoSpinner.selectedItemPosition ==0) {
                Toast.makeText(requireContext(), "Seleziona  un parametro valido per ogni sezione", Toast.LENGTH_LONG).show()
             } else {
                 fetchData(selectedType, selectedAnno, selectedMakes,CarList,outResSpinner )
            }

        }



    }



    private  suspend fun populateSpinner(item: String, spinner: Spinner, lista: MutableList<String>) {

        var scritta=""
        when (item) {
            "years" -> scritta = "Anno"
            "types" -> scritta = "Tipo"
            "makes" -> scritta = "Marchio"
        }
        lista.add(0,"Seleziona un $scritta")
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


    fun createItemSelectedListener(
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
                // Verifica se l'array non è vuoto e se la posizione è valida
                if (ListaPassata.isNotEmpty() && position >= 0 && position < ListaPassata.size) {
                    // Ottieni l'opzione selezionata
                    Log.e("AddingCarFragment","CICICO $ListaPassata" )
                    val selectedOption = ListaPassata[position]
                    Log.d("AddingCarFragment", "Elemento selezionato12345: $selectedOption")
                    when (MessaggioPerMancatoInserimento) {

                        "Tipo" -> selectedType = selectedOption
                        "Anno" -> selectedAnno = selectedOption
                        "Marchio" -> selectedMakes = selectedOption
                        "Macchina" -> selectedCar = selectedOption

                    }
                    Log.d("AddingCarFragment", "Elemento selezionato: $selectedOption")

                } else {
                    Log.e("AddingCarFragment", "Lista vuota o indice non valido")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Azioni da eseguire quando non viene selezionata alcuna opzione
            }
        }
    }







    private fun fetchData(
        tipo: String?,
        anno: String?,
        marca: String?,
        ListaMacchina:MutableList<String>,
        spinnerMacchine:Spinner


    ) {
        //Log.d("fetchData", "Il valore di anno è: $anno")
        // Il resto del tuo codice per effettuare la chiamata di rete e gestire la risposta
        //  val requestUrl = "https://car-data.p.rapidapi.com/cars?limit=1&page=0&year=$anno&make=$marca&type=$tipo&model=$modello"
        val requestUrl = "https://car-data.p.rapidapi.com/cars?limit=1&page=0&year=$anno&make=$marca&type=$tipo"

        // Esegui la chiamata API in un thread separato utilizzando coroutine

        GlobalScope.launch(Dispatchers.IO) {
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

                val response = client.newCall(request).execute()

                // Accedi al thread UI per aggiornare il TextView con la risposta

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        spinnerMacchine.visibility = View.VISIBLE
                        val responseBody = response.body?.string()
                        val responseArray = responseBody
                            ?.removeSurrounding("[", "]")
                           ?.replace("\"", "")
                            ?.replace("{", "")
                            ?.replace("}", "")
                            ?.split("]")
                            ?.toTypedArray()
                        responseArray?.let { ListaMacchina.addAll(it) }

                        withContext(Dispatchers.Main) {
                            val adapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_item,
                                ListaMacchina
                            )
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerMacchine.adapter = adapter


                        }
                        spinnerMacchine.onItemSelectedListener = createItemSelectedListener( ListaMacchina,"Macchina")
                    } else {
                        throw IOException("Errore nella richiesta: ${response.code}")
                    }
                }
            } catch (e: IOException) {
                // Accedi al thread UI per gestire l'eccezione di connessione
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Errore di connessione", Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}