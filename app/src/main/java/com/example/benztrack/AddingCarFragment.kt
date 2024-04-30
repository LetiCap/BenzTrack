package com.example.benztrack

import android.app.VoiceInteractor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment

class AddingCarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment


        val cazz= inflater.inflate(R.layout.fragment_addingcar, container, false)
/*

        var inAnno= view?.findViewById<EditText>(R.id.Anno)
        var inMarca= view?.findViewById<EditText>(R.id.Marca)
        var inTipo= view?.findViewById<EditText>(R.id.Tipo)
        var btn= view?.findViewById<Button>(R.id.btnAdd)
        var outRes= view?.findViewById<TextView>(R.id.result)


        // Puoi eseguire le operazioni necessarie con la vista ottenuta

        btn?.setOnClickListener {

            val marca = inMarca!!.text.toString()
            val anno = inAnno!!.text.toString()
            val tipo = inTipo!!.text.toString()

            // Crea una stringa concatenando i testi degli EditText
            val concatenatedText = "$marca\n$anno\n$tipo"

            // Imposta la stringa concatenata nella TextView
            outRes?.text = concatenatedText

        }*/
        return cazz
    }


        /*
        findViewById<Button>(R.id.btnAdd).setOnClickListener {

            val txtViewMarca= findViewById<EditText>(R.id.Marca).text.toString())
            val txtViewAnno = findViewBy<EditText>(R.id.Anno).text.toString())
            resultLauncher.launch()
        }



    }*/
/*
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Puoi inizializzare le tue view e gestire le interazioni UI qui
    }*/
}
