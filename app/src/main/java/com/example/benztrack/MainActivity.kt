package com.example.benztrack


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private var selectedVehicle: String? = null
    private lateinit var notification: Notification

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inizializzazione della classe Notification
        notification = Notification(this)
        notification.createNotificationChannel()

        // Richiedi il permesso di notifica all'utente se non è già abilitato
        if (!notification.areNotificationsEnabled()) {
            showEnableNotificationsDialog()
        }



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

    private fun showEnableNotificationsDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Abilita Notifiche")
            .setMessage("Per ricevere avvisi importanti, è necessario abilitare le notifiche.")
            .setPositiveButton("Abilita") { dialog, _ ->
                notification.requestNotificationPermission()
                dialog.dismiss()
            }
            .setNegativeButton("Annulla") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .show()
    }


}