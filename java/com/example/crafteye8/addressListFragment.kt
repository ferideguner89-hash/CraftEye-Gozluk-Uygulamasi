package com.example.crafteye8
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crafteye8.adapter.AddressAdapter
import com.example.crafteye8.databinding.FragmentAddressListBinding
import com.example.crafteye8.model.Address
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.IOException
import java.util.Locale // Locale importunu ekleyin

class addressListFragment : Fragment() {
    private lateinit var binding: FragmentAddressListBinding
    private val addressList = ArrayList<Address>()
    private lateinit var adapter: AddressAdapter

    private val db = FirebaseFirestore.getInstance()
    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    private lateinit var mapView: MapView
    private var selectedLat: Double? = null
    private var selectedLng: Double? = null
    private var marker: Marker? = null

    // Adres bilgilerini tutmak için
    private var selectedCity: String? = null
    private var selectedDistrict: String? = null
    private var selectedNeighborhood: String? = null
    private var apartmentInfo: String? = null // YENİ ALAN

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // İzin kontrolü ve istemi (mevcut kodunuzda var)
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
        }

        // OSMDroid config
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )

        mapView = binding.mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.isClickable = true
        mapView.isFocusable = true

        val mapController = mapView.controller
        mapController.setZoom(16.0)
        mapController.setCenter(GeoPoint(41.015137, 28.979530)) // İstanbul

        // HARİTAYA TIKLAMA → Marker koyar ve Adres Bilgilerini alır
        mapView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {

                val proj = mapView.projection
                val geoPoint = proj.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint

                selectedLat = geoPoint.latitude
                selectedLng = geoPoint.longitude

                // *** Ters Geocoding işlemini başlat ***
                getPlaceDetails(selectedLat!!, selectedLng!!)
                // **********************************************

                // Eski markerı sil
                marker?.let {
                    mapView.overlays.remove(it)
                }

                // Yeni marker
                marker = Marker(mapView).apply {
                    position = geoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Seçilen Konum"
                }

                mapView.overlays.add(marker)
                mapView.invalidate()

                // Toast'u getPlaceDetails içinden vereceğiz
            }
            false
        }

        // RecyclerView
        adapter = AddressAdapter(addressList)
        binding.rvAddresses.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAddresses.adapter = adapter

        binding.btnSaveAddress.setOnClickListener {
            saveAddress()
        }

        loadAddresses()
    }

    // *** YENİ FONKSİYON: Ters Coğrafi Kodlama ***
    private fun getPlaceDetails(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(requireContext(), Locale("tr", "TR")) // Türkçe dil ayarı

        try {
            // Geocoder'dan adres listesi alınır. maxResults: 1 en yakın sonucu ister.
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]

                // Önemli Adres Bileşenlerini Çıkar
                selectedCity = address.adminArea // Genellikle İl
                selectedDistrict = address.subAdminArea ?: address.locality
                selectedNeighborhood = address.thoroughfare ?: address.getAddressLine(0)

                // Kullanıcının görebileceği bir özet oluşturun
                val addressSummary = "$selectedCity, $selectedDistrict"

                // Otomatik Doldurma ve Temizleme
                binding.edtDetail.setText(selectedNeighborhood) // Mahalle/Sokak bilgisini detaya yaz
                binding.edtTitle.setText(addressSummary) // İl/İlçe bilgisini başlığa yaz
                // Apartman bilgisini kullanıcıdan almak için temizliyoruz

                Toast.makeText(
                    requireContext(),
                    "Konum seçildi: $addressSummary",
                    Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(requireContext(), "Konum adresi bulunamadı.", Toast.LENGTH_SHORT).show()
                selectedCity = null
                selectedDistrict = null
                selectedNeighborhood = null
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Adres servisine bağlanılamadı. İnternet bağlantınızı kontrol edin.", Toast.LENGTH_LONG).show()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Geocoding için geçersiz koordinatlar.", Toast.LENGTH_LONG).show()
        }
    }
    // **********************************************

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // İzin sonuçları (mevcut kodunuzda var)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Konum izni verildi", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Konum izni gerekli!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveAddress() {

        val title = binding.edtTitle.text.toString().trim()
        val detail = binding.edtDetail.text.toString().trim()

        // *** DÜZELTME 1: Apartment bilgisini oku ***
        // *********************************************

        // *** DÜZELTME 2: Tüm alanların boş olup olmadığını kontrol et ***


        if (selectedLat == null || selectedLng == null) {
            Toast.makeText(requireContext(), "Lütfen haritadan konum seçin", Toast.LENGTH_SHORT).show()
            return
        }

        // Firestore'a kaydedilecek verilere il/ilçe/mahalle/apartman bilgisini ekleyin
        val address = hashMapOf(
            "title" to title,
            "detail" to detail,
            "lat" to selectedLat,
            "lng" to selectedLng,
            "city" to selectedCity,
            "district" to selectedDistrict,
            "neighborhood" to selectedNeighborhood,

        )

        db.collection("users")
            .document(uid)
            .collection("addresses")
            .add(address)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Adres kaydedildi!", Toast.LENGTH_SHORT).show()

                // *** DÜZELTME 3: Alanları temizle ***
                binding.edtTitle.text.clear()
                binding.edtDetail.text.clear()
                // Apartman alanını temizle
                // ************************************

                marker?.let {
                    mapView.overlays.remove(it)
                    mapView.invalidate()
                }

                // Seçilen konum ve adres bilgilerini temizle
                selectedLat = null
                selectedLng = null
                selectedCity = null
                selectedDistrict = null
                selectedNeighborhood = null
                apartmentInfo = null // Apartman bilgisini temizle

                loadAddresses()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Adres kaydedilemedi!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadAddresses() {
        db.collection("users")
            .document(uid)
            .collection("addresses")
            .get()
            .addOnSuccessListener {

                addressList.clear()

                for (doc in it) {
                    // Address modelinizin bu yeni alanları içermesi gerekir!
                    val address = doc.toObject(Address::class.java)
                    addressList.add(address)
                }

                adapter.notifyDataSetChanged()
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
}
