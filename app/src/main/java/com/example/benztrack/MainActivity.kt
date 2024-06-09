package com.example.benztrack

import AddingCarFragment
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var selectedVehicle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //cambio fragment a seconda del tasto selezionato
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.home -> {
                    replaceFragment(HomeFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.add_car -> {
                    replaceFragment(AddingCarFragment())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.add_fuel -> {

                    val addFuelFragment = AddFuel()
                    if (selectedVehicle != null) {
                        val bundle = Bundle()
                        bundle.putString("data", selectedVehicle)
                        addFuelFragment.arguments = bundle
                    }
                    replaceFragment(addFuelFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bollo -> {

                    val addBolloFragment = AddBollo()
                    if (selectedVehicle != null) {
                        val bundle = Bundle()
                        bundle.putString("data", selectedVehicle)
                        addBolloFragment.arguments = bundle
                    }
                    replaceFragment(addBolloFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.manutenzione -> {

                    val addManutenzioneFragment = AddManutenzione()
                    if (selectedVehicle != null) {
                        val bundle = Bundle()
                        bundle.putString("data", selectedVehicle)
                        addManutenzioneFragment.arguments = bundle
                    }
                    replaceFragment(addManutenzioneFragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        // Visualizza il frammento iniziale
        replaceFragment(HomeFragment())
    }

    // Funzione per sostituire il frammento nel contenitore del frammento
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

    fun setSelectedVehicle(vehicle: String) {
        selectedVehicle = vehicle
    }



}