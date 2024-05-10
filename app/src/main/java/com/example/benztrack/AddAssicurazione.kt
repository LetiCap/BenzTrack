package com.example.benztrack

import DatabaseApp
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddAssicurazione : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_assicurazione, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val costoAssicurazione = view.findViewById<EditText>(R.id.CostoAssicurazione)
        val btnAdd = view.findViewById<Button>(R.id.AddAssurance)


        val database = DatabaseApp(requireContext())
        val nometabellaINT= 123
        val nometabellaSTRING= "123"



        btnAdd.setOnClickListener{
            val costoString = costoAssicurazione.text.toString()
            if(costoString.isNotEmpty()) {
                val costoInt = Integer.parseInt(costoString)

                database.insertValueforCar(nometabellaINT,"assicurazione",nometabellaSTRING,costoInt )
                val datatype= database.getAllData("123")
                println(datatype)
            } else {
                Toast.makeText(requireContext(), "non è stato inserito nulla ", Toast.LENGTH_SHORT).show()

            }
        }
        // You can initialize views and handle UI interactions here
    }
}