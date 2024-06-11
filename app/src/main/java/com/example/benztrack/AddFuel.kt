package com.example.benztrack

import DatabaseApp
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class AddFuel : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var rootView: View
    private val markers = mutableListOf<Marker>()

    //codice per la richiesta del permesso
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_fuel, container, false)

        // Initialize the MapView

        map(rootView, savedInstanceState)
        // Request location permission
        requestLocationPermission()

        return rootView
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val SpentOnFuel = view.findViewById<EditText>(R.id.AmountSpentOnFuel)
        val FuelCost = view.findViewById<EditText>(R.id.FuelCost)
        val CurrentKm = view.findViewById<EditText>(R.id.CurrentKm)
        val btnAdd = view.findViewById<Button>(R.id.Add)

        val btnGraf = view.findViewById<Button>(R.id.OpenGraf)
        val selectedVehicleHome = arguments?.getString("data")
        val txtVehicle = view.findViewById<TextView>(R.id.txtVehicle)
        val txtConsMedio = view.findViewById<TextView>(R.id.txtConsMedio)

        if (selectedVehicleHome == null) {
            Toast.makeText(requireContext(), "Nessun veicolo selezionato", Toast.LENGTH_SHORT).show()
            return
        }

        txtVehicle.text = "Veicolo selezionato: $selectedVehicleHome"


        val database = DatabaseApp(requireContext())
        val tableName: String = selectedVehicleHome.toString()






        btnAdd.setOnClickListener {
            val SpentString = SpentOnFuel.text.toString()
            val FuelString = FuelCost.text.toString()
            val KmString = CurrentKm.text.toString()


            if (SpentString.isNotEmpty() && FuelString.isNotEmpty() && KmString.isNotEmpty()) {
                val SpentDouble = SpentString.toDouble()
                val FuelDouble = FuelString.toDouble()
                val KmDouble = KmString.toDouble()
                database.insertValueforCar("benzina", tableName, SpentDouble)
                database.insertValueforCar("CostoBenzina", tableName, FuelDouble)
                database.insertValueforCar("KM", tableName, KmDouble)

                val averageConsumption = calculateAverageConsumption(database, tableName)
                txtConsMedio.text = "Average consumation: $averageConsumption km/l"



            } else {
                Toast.makeText(requireContext(),"Inserire i dati mancanti",Toast.LENGTH_SHORT).show()
            }
        }

        btnGraf.setOnClickListener{
            Toast.makeText(requireContext(), "Hai premuto il pulsante per visualizzare i grafici", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), FuelGraphs::class.java)
            intent.putExtra("veicolo selezionato", tableName)
            startActivity(intent)
        }

    }
    private fun calculateAverageConsumption(database: DatabaseApp, tableName: String): Double {
        val totalFuel = database.getSumColumn("benzina", tableName)
        val totalKm = database.getSumColumn("KM", tableName)
        return if (totalFuel > 0) totalKm / totalFuel else 0.0
    }



    private fun map(rootView: View, savedInstanceState: Bundle?) {
        mapView = rootView.findViewById(R.id.fuelMap)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Inizializza il client per ottenere la posizione
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        // Richiedi aggiornamento location
        requestLocationUpdates()
    }



    // Funzione per richiedere il permesso di accesso alla posizione
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Se il permesso non è stato concesso, richiedilo all'utente
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    // Funzione per ottenere la posizione attuale dell'utente
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("getCurrentLocation", "I permessi non sono concessi, esci dalla funzione")

            return
        }

        Log.d("AddFuel", "getCurrentLocation() called")
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = location.latitude
                val longitude = location.longitude
                saveLocationData(latitude, longitude)
                Log.d("AddFuel", "Current location: $latitude, $longitude")
                // Aggiungi un marker sulla mappa nella posizione attuale
                val currentPosition = LatLng(latitude, longitude)
                val marker =googleMap.addMarker(
                    MarkerOptions()
                        .position(currentPosition)
                        .title("My Current Position")
                )

                // Abilita la geolocalizzazione sulla mappa
                requestLocationUpdates()

                // Muovi la camera per centrare il marker
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13f))
                if (marker != null) {
                    markers.add(marker)
                }
            }

        }
    }

    private fun saveLocationData(latitude: Double, longitude: Double) {
        val sharedPreferences =
            requireContext().getSharedPreferences("location_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("latitude", latitude.toFloat())
        editor.putFloat("longitude", longitude.toFloat())
        editor.apply()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se il permesso è stato concesso, ottieni la posizione attuale
                getCurrentLocation()

            } else {
                // Permesso negato, mostra un messaggio all'utente
                Toast.makeText(
                    requireContext(),
                    "Per ottenere la posizione attuale è necessario concedere il permesso",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }



    private fun requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
