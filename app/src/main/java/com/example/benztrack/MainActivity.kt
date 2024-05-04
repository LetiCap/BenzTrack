package com.example.benztrack

import AddingCarFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

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
                R.id.add_fuel ->{
                    replaceFragment(Fuel())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.bollo -> {
                    replaceFragment(AddBollo())
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.assicurazione ->{
                    replaceFragment(AddAssicurazione())
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

}
