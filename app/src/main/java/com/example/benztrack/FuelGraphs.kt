package com.example.benztrack

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FuelGraphs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fuel_graphs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                val fragmentManager = supportFragmentManager
                val fragment = fragmentManager.findFragmentByTag("AddFuel")
                if (fragment == null) {
                    // Il fragment AddFuel non è presente nello stack, aggiungilo
                    fragmentManager.beginTransaction()
                        .replace(R.id.add_fuel, AddFuel(), "AddFuel")
                        .addToBackStack(null)
                        .commit()
                } else {
                    // Il fragment AddFuel è già presente nello stack, torna indietro
                    fragmentManager.popBackStack()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}