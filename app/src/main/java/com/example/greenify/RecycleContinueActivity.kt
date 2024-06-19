package com.example.greenify

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import java.io.IOException


class RecycleContinueActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var lastLocation: Location
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var currentLocation: EditText
    private lateinit var district: EditText
    private lateinit var apartmentNumber: EditText
    private lateinit var floorNumber: EditText
    private lateinit var latitudeEditText: EditText
    private lateinit var longitudeEditText: EditText
    private lateinit var note: EditText




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_continue)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)




        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val continueButton: Button = findViewById(R.id.continueButton)

        val map = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        currentLocation = findViewById(R.id.currentLocation)
        district = findViewById(R.id.district)
        apartmentNumber = findViewById(R.id.apartmentNumber)
        floorNumber = findViewById(R.id.floorNumber)
        note = findViewById(R.id.note)
        latitudeEditText = findViewById(R.id.latitude)
        longitudeEditText = findViewById(R.id.longitude)

        continueButton.setOnClickListener {

            val selectedMaterial = intent.getStringExtra("selected_material")
            val weight = intent.getStringExtra("weight")

            val location = currentLocation.text.toString()
            val district = district.text.toString()
            val apartmentNumber = apartmentNumber.text.toString()
            val floorNumber = floorNumber.text.toString()
            val note = note.text.toString()

            if (apartmentNumber.isNotEmpty() && floorNumber.isNotEmpty()) {
                val intent = Intent(this, RecyclePreview::class.java).apply {
                    putExtra("selected_material", selectedMaterial)
                    putExtra("weight", weight)
                    putExtra("location", location)
                    putExtra("district", district)
                    putExtra("apartmentNumber", apartmentNumber)
                    putExtra("floorNumber", floorNumber)
                    putExtra("note", note)
                    putExtra("latitude", lastLocation.latitude)
                    putExtra("longitude", lastLocation.longitude)
                }
                startActivity(intent)
            }
            else {
                Toast.makeText(this, "Lütfen bina ve kat numaranızı girin", Toast.LENGTH_SHORT).show()

            }


        }

        currentLocation = findViewById(R.id.currentLocation)
        getDeviceLocation()

    }

    private fun getDeviceLocation() {
        try {
            // Konumu al
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {

                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Konumu EditText'e yazdır
                    val address = getLocationAddress(location)
                    currentLocation.setText(address)

                    latitudeEditText.setText(latitude.toString())
                    longitudeEditText.setText(longitude.toString())


                    setDistrictFromLocation(location)

                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun getLocationAddress(location: Location): String {
        val geocoder = Geocoder(this)
        var addressText = ""
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    setDistrictFromLocation(location)
                    addressText = address.getAddressLine(0)
                }
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        return addressText
    }

    private fun setDistrictFromLocation(location: Location) {
        val geocoder = Geocoder(this)
        try {
            val addresses: MutableList<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val districtName = address.subAdminArea ?: address.locality // İlçe bilgisini al

                district.setText(districtName) // "district" değişkeni EditText nesnesi olduğu için setText fonksiyonu kullanılır
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED){
            return
        }

        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)

                // Latitude ve longitude değerlerini al
                val latitude = location.latitude
                val longitude = location.longitude

                placeMarketOnMap(currentLatLong)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
    }

    private fun placeMarketOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        mMap.addMarker(markerOptions)

    }

    override fun onMarkerClick(p0: Marker) = false

}
