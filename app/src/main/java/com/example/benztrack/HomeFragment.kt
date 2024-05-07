package com.example.benztrack

import DatabaseApp
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inizializzazione dello Spinner
        val vehicleSpinner: Spinner = view.findViewById(R.id.veichleSpinner)





        // Lista di veicoli
        val vehicleList = listOf("Auto", "Auto2", "Auto3")

        // Creazione dell'adapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, vehicleList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Collegamento dell'adapter allo Spinner
        vehicleSpinner.adapter = adapter

        // Gestione dell'evento di selezione dello Spinner
        vehicleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
}
