package com.example.benztrack

import android.app.VoiceInteractor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException


class AddingCarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_addingcar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var inAnno = view.findViewById<EditText>(R.id.Anno)
        var inMarca = view.findViewById<EditText>(R.id.Marca)
        var inTipo = view.findViewById<EditText>(R.id.Tipo)
        var btn = view.findViewById<Button>(R.id.btnAdd)
        var outRes = view.findViewById<TextView>(R.id.result)



        btn.setOnClickListener {

            val marca = inMarca.text.toString()
            val anno = inAnno.text.toString()
            val tipo = inTipo.text.toString()
            /*
            if (marca.isBlank() || anno.isBlank() || tipo.isBlank()) {
                Toast.makeText(requireContext(), "inserire tutti i parametri", Toast.LENGTH_LONG)
                    .show()

            } else {*/
                // Crea una stringa concatenando i testi degli EditText
                fetchData(tipo, anno, marca, outRes)
                /*val concatenatedText = "$marca $anno $tipo"
                outRes.text = concatenatedText*/
           // }

        }
    }

    private fun fetchData(tipo: String, anno: String, marca: String, testo: TextView) {
        val requestUrl =
            "https://car-data.p.rapidapi.com/cars?limit=10&page=0&marca=$marca&anno=$anno&tipo=$tipo"

        // Esegui la chiamata API in un thread separato
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

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    // Puoi manipolare il corpo della risposta qui
                    // Assicurati di eseguire operazioni sull'interfaccia utente sul thread UI principale

                    activity?.runOnUiThread {
                        // Esempio di visualizzazione del corpo della risposta in un TextView
                        testo.text = responseBody
                    }
                } else {
                    // Gestisci il caso in cui la richiesta non ha avuto successo
                    activity?.runOnUiThread {
                        // Esempio di visualizzazione di un messaggio di errore in un TextView
                        testo.text = "Errore nella richiesta: ${response.code}"
                    }
                }
            } catch (e: IOException) {
                // Gestisci l'eccezione IO (ad esempio, connessione di rete interrotta)
                e.printStackTrace()
            }
        }
    }
}