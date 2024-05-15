package com.example.benztrack

import DatabaseApp
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddFuel : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var rootView: View
    lateinit var lineChart: LineChart

    //codice per la richiesta del permesso
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView= inflater.inflate(R.layout.fragment_fuel, container, false)


        currentLocation(rootView)

        map(rootView, savedInstanceState)


        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val costoFuel = view.findViewById<EditText>(R.id.CostoFuel)
        val btnAdd = view.findViewById<Button>(R.id.Add)
      //  lineChart = view.findViewById(R.id.linechart)

        val database = DatabaseApp(requireContext())
        val tableName = "t123"

        val datidaldatabase=getDataFromDatabase(database, tableName)
     //  updateLineChart(datidaldatabase)


        btnAdd.setOnClickListener {
            val costoString = costoFuel.text.toString()
            if (costoString.isNotEmpty()) {
                val costoInt = costoString.toInt()
                database.insertValueforCar("benzina", tableName, costoInt)
              //  val newEntries = getDataFromDatabase(database, tableName)
           //     updateLineChart(newEntries)

            } else {
                Toast.makeText(requireContext(), "Inserire il costo della benzina", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun getDataFromDatabase(
        database: DatabaseApp,
        tableName: String
    ): ArrayList<Entry> {
        val entries = ArrayList<Entry>()

        val dataFromDatabase = database.getDataColumnInt("benzina", tableName)

        // Itera sui dati ottenuti dal database e crea oggetti Entry
        for ((index, value) in dataFromDatabase.withIndex()) {
            val entry = Entry(index.toFloat(), value.toFloat())
            entries.add(entry)
        }

        return entries
    }


    private fun updateLineChart(entries: ArrayList<Entry>) {
        val dataSet = LineDataSet(entries, "BENZINA")
        val lineData = LineData(dataSet)
        lineChart.data = lineData

        // Configura l'asse x per visualizzare le date come etichette personalizzate

        lineChart.invalidate()
    }
    private fun currentLocation(rootView: View) {

        val currentPosition = rootView.findViewById<FloatingActionButton>(R.id.currentPosition)
        currentPosition.setOnClickListener {
            // Controlla se il permesso è stato già concesso
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Il permesso è già stato concesso, ottieni la posizione attuale
                getCurrentLocation()
            } else {
                // Il permesso non è stato concesso, richiedilo all'utente
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
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

        // Richiedi il permesso di accesso alla posizione
        requestLocationPermission()
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
        } else {
            // Se il permesso è già stato concesso, ottieni la posizione attuale
            getCurrentLocation()
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

            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val latitude = location.latitude
                val longitude = location.longitude
                saveLocationData(latitude, longitude)

                // Aggiungi un marker sulla mappa nella posizione attuale
                val currentPosition = LatLng(latitude, longitude)
                googleMap.addMarker(
                    MarkerOptions().position(currentPosition).title("La mia posizione attuale")
                )

                // Muovi la camera per centrare il marker
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 13f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se il permesso è stato concesso, ottieni la posizione attuale
                getCurrentLocation()
            } else {
                //toast per avviso permesso negato
            }
        }
    }

    private fun saveLocationData(latitude: Double, longitude: Double) {
        val sharedPreferences = requireContext().getSharedPreferences("location_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("latitude", latitude.toFloat())
        editor.putFloat("longitude", longitude.toFloat())
        editor.apply()
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
