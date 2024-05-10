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


class AddBollo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_bollo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val costoBollo = view.findViewById<EditText>(R.id.CostoBollo)
        val btnAdd = view.findViewById<Button>(R.id.Add)


        val database = DatabaseApp(requireContext())
        val nometabellaINT= 123
        val nometabellaSTRING= "123"



        btnAdd.setOnClickListener{
            val costoString = costoBollo.text.toString()
            if(costoString.isNotEmpty()) {
                val costoInt = Integer.parseInt(costoString)
                database.createTableInfoVehicle("123")
                database.insertValueforCar(nometabellaINT,"bollo",nometabellaSTRING,costoInt )
                val datatype= database.getAllData("123")
                println(datatype)
            } else {
                Toast.makeText(requireContext(), "non è stato inserito nulla ", Toast.LENGTH_SHORT).show()

            }
        }
        // You can initialize views and handle UI interactions here
    }
}