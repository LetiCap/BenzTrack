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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class AddingCarFragment : Fragment() {
    private var selectedModel: String = ""
    private var selectedAnno: String = ""
    private var selectedType: String = ""
    private var selectedMark: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_addingcar, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ModelloSpinner: Spinner = view.findViewById(R.id.Modello)
        val TipoSpinner: Spinner = view.findViewById(R.id.Tipo)
        val AnnoSpinner: Spinner = view.findViewById(R.id.Anno)
        val MarchioSpinner: Spinner = view.findViewById(R.id.Marchio)
        val btn = view.findViewById<Button>(R.id.btnAdd)
        val outRes = view.findViewById<TextView>(R.id.result)


        val ModellliList = listOf("Seleziona un Modello", "ProMaster City", "Moto", "Bicicletta")
        val adapterModelloSpinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ModellliList)
        adapterModelloSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ModelloSpinner.adapter = adapterModelloSpinner

        val TipoList = listOf("Seleziona un Tipo", "Van/Minivan", "Moto", "Bicicletta")
        val adapterTipoSpinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, TipoList)
        adapterTipoSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        TipoSpinner.adapter = adapterTipoSpinner

        val AnnoiList = listOf("Seleziona un Anno", "2019", "Moto", "Bicicletta")
        val adapterAnnoSpinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, AnnoiList)
        adapterAnnoSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        AnnoSpinner.adapter = adapterAnnoSpinner

        val MarchioList = listOf("Seleziona un Marchio", "Ram", "Moto", "Bicicletta")
        val adapterMarchioSpinner =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, MarchioList)
        adapterMarchioSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        MarchioSpinner.adapter = adapterMarchioSpinner



        // Imposta il listener per lo spinner Tipo
        // Imposta il listener per lo spinner Marchio
        // Imposta il listener per lo spinner Anno
        // Imposta il listener per lo spinner Modello
        TipoSpinner.onItemSelectedListener = createItemSelectedListener(TipoList, "Tipo")
        MarchioSpinner.onItemSelectedListener = createItemSelectedListener(MarchioList, "Marchio")
        AnnoSpinner.onItemSelectedListener = createItemSelectedListener(AnnoiList, "Anno")
        ModelloSpinner.onItemSelectedListener = createItemSelectedListener(ModellliList, "Modello")


        btn.setOnClickListener {
            if(selectedType.isBlank() || selectedAnno.isBlank() || selectedMark.isBlank() || selectedModel.isBlank()) {
                Toast.makeText(requireContext(), "inserire tutti i parametri", Toast.LENGTH_LONG)
                    .show()
            }else{fetchData(selectedType, selectedAnno, selectedMark, selectedModel, outRes)}



        }



    }


    fun createItemSelectedListener(ListaPassata: List<String>, MessaggioPerMancatoInserimento: String): AdapterView.OnItemSelectedListener {
        return object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Ignora la selezione se l'utente seleziona il suggerimento


                    // Ottieni l'opzione selezionata
                val selectedOption = ListaPassata[position]
                if(position!=0) {
                    when (MessaggioPerMancatoInserimento) {
                        "Modello" -> selectedModel = selectedOption
                        "Tipo" -> selectedType = selectedOption
                        "Anno" -> selectedAnno = selectedOption
                        "Marchio" -> selectedMark = selectedOption
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Azioni da eseguire quando non viene selezionata alcuna opzione
            }
        }
    }
}


        /*


        var inAnno = view.findViewById<EditText>(R.id.Anno)
        var inMarca = view.findViewById<EditText>(R.id.Marchio)
        var inTipo = view.findViewById<EditText>(R.id.Tipo)
        var btn = view.findViewById<Button>(R.id.btnAdd)
        var outRes = view.findViewById<TextView>(R.id.result)



        btn.setOnClickListener {

            val marca = inMarca.text.toString()
            val anno = inAnno.text.toString()
            val tipo = inTipo.text.toString()

            if (marca.isBlank() || anno.isBlank() || tipo.isBlank()) {
                Toast.makeText(requireContext(), "inserire tutti i parametri", Toast.LENGTH_LONG)
                    .show()

            } else {
                // Crea una stringa concatenando i testi degli EditText
                //val concatenatedText = "$marca $anno $tipo 1"
               // outRes.text = concatenatedText
                fetchData(tipo, anno, marca, outRes)

            }

        }
        */



    private fun fetchData(tipo: String?, anno: String?, marca: String?,modello: String?, testo: TextView) {
        Log.d("fetchData", "Il valore di anno è: $anno")
        // Il resto del tuo codice per effettuare la chiamata di rete e gestire la risposta
        val requestUrl = "https://car-data.p.rapidapi.com/cars?limit=1&page=0&year=$anno&make=$marca&type=$tipo&model=$modello"
       // "https://car-data.p.rapidapi.com/cars/years"

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
                        testo.text = response.body?.string()
                    } else {
                        testo.text = "Errore nella richiesta: ${response.code}"
                    }
                }
            } catch (e: IOException) {
                // Accedi al thread UI per gestire l'eccezione di connessione
                withContext(Dispatchers.Main) {
                    testo.text = "Errore di connessione: ${e.message}"
                }
            }
        }
    }