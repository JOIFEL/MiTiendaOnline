package com.example.mitiendaonline.fragments


import android.graphics.drawable.Drawable

import android.location.Location

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


import com.example.mitiendaonline.R
import com.example.mitiendaonline.activities.MainActivity
import com.example.mitiendaonline.data.model.MainViewModel

import com.example.mitiendaonline.databinding.FragmentTiendasMapaBinding


import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


import java.util.Locale

class TiendasMapaFragment : Fragment() {

    private var _binding: FragmentTiendasMapaBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: MapView
    private lateinit var mainViewModel: MainViewModel
    private var myLocationOverlay: MyLocationNewOverlay? = null
    private var storesOverlay: ItemizedIconOverlay<OverlayItem>? = null


    private val ubicacionesTiendas = listOf(
        TiendaInfo(
            nombre = "Mi Tienda Central",
            direccion = "Cra. 22a #172-45",
            latitud = 4.753013,
            longitud = -74.044503
        ),
        TiendaInfo(
            nombre = "Tienda Norte",
            direccion = "Av. 19 # 120-20, Bogotá",
            latitud = 4.706780,
            longitud = -74.038590
        ),
        TiendaInfo(
            nombre = "Tienda Sur",
            direccion = "Carrera 30 # 3-10 Sur, Bogotá",
            latitud = 4.580450,
            longitud = -74.120610
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = requireContext()
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = requireContext().packageName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTiendasMapaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        map = binding.mapView
        setupMap()
        setupMapOverlays()

        binding.progressBarMap.visibility = View.VISIBLE

        mainViewModel.ubicacionActual.observe(viewLifecycleOwner) { coordenadas ->
            binding.progressBarMap.visibility = View.GONE
            coordenadas?.let {
                Log.d("TiendasMapaFragment", "Ubicación del usuario actualizada: ${it.latitud}, ${it.longitud}")
                val userGeoPoint = GeoPoint(it.latitud, it.longitud)
                map.controller.animateTo(userGeoPoint)
                map.controller.setZoom(14.0)

                addStoreMarkers()
                map.invalidate()
            } ?: run {
                Log.d("TiendasMapaFragment", "No hay ubicación del usuario disponible en ViewModel.")
                Toast.makeText(requireContext(), "No se pudo obtener tu ubicación. Por favor, activa los permisos y el GPS.", Toast.LENGTH_LONG).show()
                (requireActivity() as? MainActivity)?.checkAndRequestLocationPermissions()
            }
        }

        addStoreMarkers()

        mainViewModel.ubicacionActual.value?.let {
            val userGeoPoint = GeoPoint(it.latitud, it.longitud)
            map.controller.setCenter(userGeoPoint)
            map.controller.setZoom(14.0)
            binding.progressBarMap.visibility = View.GONE
        } ?: run {
            val bogotaCenter = GeoPoint(4.65, -74.05)
            map.controller.setCenter(bogotaCenter)
            map.controller.setZoom(12.0)
            binding.progressBarMap.visibility = View.GONE
        }
    }

    private fun setupMap() {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(12.0)

        val copyrightOverlay = CopyrightOverlay(requireContext())
        copyrightOverlay.setTextSize(10)
        map.overlays.add(copyrightOverlay)
    }

    private fun setupMapOverlays() {
        val compassOverlay = CompassOverlay(requireContext(), InternalCompassOrientationProvider(requireContext()), map)
        compassOverlay.enableCompass()
        map.overlays.add(compassOverlay)

        val rotationGestureOverlay = RotationGestureOverlay(map)
        rotationGestureOverlay.setEnabled(true)
        map.overlays.add(rotationGestureOverlay)

        myLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), map)
        myLocationOverlay?.apply {
            enableMyLocation()
            disableFollowLocation()
            isDrawAccuracyEnabled = true
        }
        map.overlays.add(myLocationOverlay)
    }

    private fun addStoreMarkers() {
        map.overlays.remove(storesOverlay)
        val items = ArrayList<OverlayItem>()

        val defaultMarkerIcon: Drawable? = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_store)

        val nearestMarkerIcon: Drawable? = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_store_blue) // Necesitas crear este drawable

        if (nearestMarkerIcon == null) {
            Log.e("TiendasMapaFragment", "Error: No se encontró el drawable ic_store_marker_blue_large. Usando el por defecto.")
        }

        if (defaultMarkerIcon == null) {
            Log.e("TiendasMapaFragment", "Error: No se encontró el drawable ic_store_marker.")
            return
        }

        // Obtener la ubicación del usuario del ViewModel
        val userCoords = mainViewModel.ubicacionActual.value
        var closestStore: TiendaInfo? = null
        var minDistance = Float.MAX_VALUE

        userCoords?.let { userLatLong ->
            val userLocation = Location("user_provider").apply {
                latitude = userLatLong.latitud
                longitude = userLatLong.longitud
                time = System.currentTimeMillis()
            }

            for (tienda in ubicacionesTiendas) {
                val tiendaLocation = Location("store_provider").apply {
                    latitude = tienda.latitud
                    longitude = tienda.longitud
                    time = System.currentTimeMillis()
                }
                val distance = userLocation.distanceTo(tiendaLocation)
                if (distance < minDistance) {
                    minDistance = distance
                    closestStore = tienda
                }
            }
        }

        for (tienda in ubicacionesTiendas) {
            val geoPoint = GeoPoint(tienda.latitud, tienda.longitud)
            val item = OverlayItem(tienda.nombre, tienda.direccion, geoPoint)

            if (tienda == closestStore && nearestMarkerIcon != null) {

                item.setMarker(nearestMarkerIcon)

            } else {

                item.setMarker(defaultMarkerIcon)
            }
            items.add(item)
        }

        storesOverlay = ItemizedIconOverlay(
            items,
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                    item?.let {
                        val tiendaInfo = ubicacionesTiendas[index]
                        val distanciaTexto: String

                        val userCurrentCoords = mainViewModel.ubicacionActual.value
                        if (userCurrentCoords != null) {
                            val userLocation = Location("user_provider").apply {
                                latitude = userCurrentCoords.latitud
                                longitude = userCurrentCoords.longitud
                                time = System.currentTimeMillis()
                            }
                            val tiendaLocation = Location("store_provider").apply {
                                latitude = tiendaInfo.latitud
                                longitude = tiendaInfo.longitud
                                time = System.currentTimeMillis()
                            }
                            val distanciaMetros = userLocation.distanceTo(tiendaLocation)
                            distanciaTexto = String.format(Locale.getDefault(), "A %.2f km de ti.", distanciaMetros / 1000)
                        } else {
                            distanciaTexto = "Ubicación del usuario no disponible para calcular distancia."
                        }

                        Toast.makeText(
                            requireContext(),
                            "${item.title}\n${item.snippet}\n$distanciaTexto",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                    return false
                }
            },
            requireContext()
        )
        map.overlays.add(storesOverlay)
        map.invalidate()
    }

    data class TiendaInfo(
        val nombre: String,
        val direccion: String,
        val latitud: Double,
        val longitud: Double
    )

    override fun onResume() {
        super.onResume()
        map.onResume()
        myLocationOverlay?.enableMyLocation()

        (requireActivity() as? MainActivity)?.checkAndRequestLocationPermissions()
        binding.progressBarMap.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
        myLocationOverlay?.disableMyLocation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        map.onDetach()
    }
}