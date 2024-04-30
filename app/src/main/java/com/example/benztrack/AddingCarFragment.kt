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
            if (marca.isBlank() || anno.isBlank() || tipo.isBlank()) {
                Toast.makeText(requireContext(), "inserire tutti i parametri", Toast.LENGTH_LONG)
                    .show()

            } else {
                // Crea una stringa concatenando i testi degli EditText
                val concatenatedText = "$marca $anno $tipo"
                outRes.text = concatenatedText
            }

        }
    }




}