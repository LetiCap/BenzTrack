package com.example.benztrack

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton


class AddFuel : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var rootView: View

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
