package com.example.benztrack

import android.app.VoiceInteractor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class AddingCarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment



        return inflater.inflate(R.layout.fragment_addingcar, container, false)
    }
        /*
        findViewById<Button>(R.id.btnAdd).setOnClickListener {

            val txtViewMarca= findViewById<EditText>(R.id.Marca).text.toString())
            val txtViewAnno = findViewBy<EditText>(R.id.Anno).text.toString())
            resultLauncher.launch()
        }

        val userInput = editText.text.toString()
        val apiUrl = "https://car-data.p.rapidapi.com/cars?limit=10&page=0&query=$userInput"
        val request = Request.Builder()
            .url(apiUrl)
            .get()
            .addHeader("X-RapidAPI-Key", "3d2a9c66e1msh98394268003597ep10489bjsn42dc7dfe3373")
            .addHeader("X-RapidAPI-Host", "car-data.p.rapidapi.com")
            .build()

    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Puoi inizializzare le tue view e gestire le interazioni UI qui
    }
}
