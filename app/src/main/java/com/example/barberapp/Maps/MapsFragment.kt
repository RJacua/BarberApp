package com.example.barberapp.Maps

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.barberapp.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentMapsBinding
    private val viewModel: MapsViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var googleMap: GoogleMap? = null
    private var selectedBarbershop: LatLng? = null
    private var userLocation: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the location client to fetch the user's current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Retrieve the map fragment and set up the callback for when the map is ready
        val mapFragment = childFragmentManager.findFragmentById(com.example.barberapp.R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        setupSpinner()

        binding.showRouteButton.setOnClickListener {
            // Check if user location and selected barbershop are set before drawing the route
            val origin = userLocation
            if (origin == null || selectedBarbershop == null) {
                return@setOnClickListener
            }
            drawRoute(origin, selectedBarbershop!!)
        }
    }

    /**
     * Called when the map is ready to be used.
     * Enables location features and fetches the user's location.
     *
     * @param map The Google Map instance that is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        enableMyLocation()
        getUserLocation()  // Fetch the user's location as soon as the map is ready
    }

    /**
     * Enables location tracking on the map if permissions are granted.
     * If not, requests the necessary permissions from the user.
     */
    private fun enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        googleMap?.isMyLocationEnabled = true
    }

    /**
     * Handles the result of the permission request.
     * If permission is granted, enables location tracking on the map.
     *
     * @param requestCode The request code for permission.
     * @param permissions The requested permissions.
     * @param grantResults The results of the permission request.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        }
    }

    /**
     * Fetches the last known location of the user and moves the camera to their position.
     */
    private fun getUserLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener(requireActivity()) { location ->
            location?.let {
                userLocation = LatLng(it.latitude, it.longitude)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation!!, 15f))
            }
        }
    }

    /**
     * Sets up the dropdown menu (Spinner) to display the list of available barbershops.
     * When a barbershop is selected, its location is updated on the map.
     */
    private fun setupSpinner() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mutableListOf<String>())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.BarberShopList.adapter = adapter

        viewModel.barbershops.observe(viewLifecycleOwner) { barbershops ->
            if (!barbershops.isNullOrEmpty()) {
                adapter.clear()
                adapter.addAll(barbershops.map { it.name })
                adapter.notifyDataSetChanged()
            }
        }

        binding.BarberShopList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                viewModel.barbershops.value?.get(position)?.let { barbershop ->
                    selectedBarbershop = LatLng(barbershop.latitude, barbershop.longitude)
                    updateMap(selectedBarbershop!!, barbershop.name)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /**
     * Updates the map by clearing previous markers and adding a new one for the selected location.
     * Also moves the camera to focus on the selected barbershop.
     *
     * @param location The coordinates of the selected barbershop.
     * @param title The name of the barbershop.
     */
    private fun updateMap(location: LatLng, title: String) {
        googleMap?.apply {
            clear()
            addMarker(MarkerOptions().position(location).title(title))
            animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    /**
     * Requests directions from Google Maps API and draws a route between the user's location and the selected barbershop.
     *
     * @param origin The starting point of the route (user's location).
     * @param destination The endpoint of the route (selected barbershop).
     */
    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving" +
                "&key=AIzaSyAiTlftHVk1KSldjZGvyAVkLfcO0G8BtfU"

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle request failure
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) return
                response.body()?.string()?.let { jsonResponse ->
                    val path = parseDirections(jsonResponse)
                    activity?.runOnUiThread {
                        googleMap?.addPolyline(PolylineOptions().addAll(path).color(Color.BLUE).width(10f))
                    }
                }
            }
        })
    }

    /**
     * Parses the JSON response from Google Maps API and extracts the route.
     *
     * @param jsonResponse The JSON response containing route details.
     * @return A list of LatLng coordinates representing the path.
     */
    private fun parseDirections(jsonResponse: String): List<LatLng> {
        val path = mutableListOf<LatLng>()
        val jsonObject = JSONObject(jsonResponse)
        val routes = jsonObject.getJSONArray("routes")
        if (routes.length() > 0) {
            val overviewPolyline = routes.getJSONObject(0).getJSONObject("overview_polyline")
            val encodedPolyline = overviewPolyline.getString("points")
            path.addAll(decodePolyline(encodedPolyline))
        }
        return path
    }

    /**
     * Decodes a polyline string into a list of LatLng coordinates.
     *
     * @param encoded The encoded polyline string.
     * @return A list of LatLng points representing the decoded path.
     */
    private fun decodePolyline(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            poly.add(LatLng(lat / 1E5, lng / 1E5))
        }
        return poly
    }
}


