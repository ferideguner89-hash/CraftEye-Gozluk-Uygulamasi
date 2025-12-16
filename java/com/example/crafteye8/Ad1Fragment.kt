package com.example.crafteye8

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import org.json.JSONObject


// Navigation Component (İhtiyaç duyulursa)
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

// Firebase Firestore
import com.google.firebase.firestore.ktx.firestore


// Görsel Yükleme Kütüphanesi (Glide)
import com.bumptech.glide.Glide

// Projenizin Paketine Ait Importlar
import com.example.crafteye8.R
import com.example.crafteye8.databinding.FragmentAd1Binding
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Kendi Product data class'ınızın yolu (Varsayımsal)

// Eğer Product modelini başka bir pakette tanımladıysanız yolu düzenleyin.
// Örneğin: 'com.example.crafteye8.model.Product'

class Ad1Fragment : Fragment() {
    private var _binding: FragmentAd1Binding? = null
    private val binding get() = _binding!!

    // Firebase Firestore
    private val db = Firebase.firestore
    private val productsCollection = db.collection("products")

    // RecyclerView ve Adapter
    private lateinit var productAdapter: ProductAdapter
    private var firestoreListener: ListenerRegistration? = null
    private var firebaseProductList = ArrayList<Product>()
    private lateinit var sharedViewModel: SharedViewModel

    // Admin kontrolü
    private val isAdmin = true // true yaparsan admin, false yaparsan normal kullanıcı

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAd1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup
        setupListeners()
        setupImagePreviewListener()
        binding.imgProduct.setImageResource(R.drawable.outline_image_24)
        loadDailyCurrencyRates()

        // RecyclerView
        setupRecyclerView()
        fetchAndListenToProducts()

        // Seçilen ürünü dinle (Bu, tıklama ile alanları doldurur)
        observeSelectedProduct()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        firestoreListener?.remove()
        _binding = null
    }

    private fun setupListeners() {
        binding.btnSave.setOnClickListener { performSave() }
        // Güncelleme butonuna performUpdate() işlevini atıyoruz
        binding.btnUpdate.setOnClickListener {
            performUpdate() // Şimdi güncelleme işlevini çağırıyoruz
        }

    }

    // *** YENİ İŞLEV: Seçilen ürünü dinleme ve alanları doldurma ***
    private fun observeSelectedProduct() {
        sharedViewModel.selectedProduct.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                // Seçilen ürün bilgilerini giriş alanlarına doldur
                binding.edtProductName.setText(product.name)
                binding.edtProductPrice.setText(product.price?.toString() ?: "")
                binding.edtProductImageUrl.setText(product.imageUrl)
                binding.edtProductGender.setText(product.gender)
                binding.edtProductFeatures.setText(product.features)
                // Null kontrolü ile indirim fiyatını doldur
                binding.edtDiscountPrice.setText(product.discountPrice?.toString() ?: "")
                // Stok bilgisini doldur
                binding.edtStock.setText(product.stock.toString())

                // Görseli yükle (setupImagePreviewListener zaten URL değişikliğini izliyor)
                // Yine de tetiklemek için manuel olarak da yükleyebiliriz:
                if (product.imageUrl.startsWith("http")) {
                    Glide.with(requireContext())
                        .load(product.imageUrl)
                        .placeholder(R.drawable.outline_image_24)
                        .error(R.drawable.outline_image_24)
                        .centerCrop()
                        .into(binding.imgProduct)
                } else {
                    binding.imgProduct.setImageResource(R.drawable.outline_image_24)
                }

                // Kullanıcıya bilgi ver
                Toast.makeText(requireContext(), "${product.name} düzenlenmek üzere seçildi.", Toast.LENGTH_SHORT).show()
            } else {
                // Ürün seçimi kaldırıldığında (örneğin güncelleme sonrası) alanları temizle
                clearInputFields()
            }
        }
    }
    // *******************************************************************

    private fun setupRecyclerView() {
        // ProductAdapter'ın ürün tıklandığında sharedViewModel.selectedProduct'ı güncellediğini varsayıyoruz.
        productAdapter = ProductAdapter(
            productList = firebaseProductList,
            sharedViewModel = sharedViewModel,
            isFavoriteScreen = false,
            isAdmin = isAdmin,
            onDeleteProduct = { product -> deleteProduct(product.id) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }
    }

    private fun fetchAndListenToProducts() {
        firestoreListener = productsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FIRESTORE", "Veri dinleme hatası: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val updatedProducts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.apply { id = doc.id }
                }
                firebaseProductList.clear()
                firebaseProductList.addAll(updatedProducts)
                productAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun deleteProduct(productId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.collection("products").document(productId).delete().await()
                withContext(Dispatchers.Main) {
                    // Silinen ürün seçili ise, seçimi kaldır
                    if (sharedViewModel.selectedProduct.value?.id == productId) {
                        sharedViewModel.selectedProduct.value = null
                    }
                    Toast.makeText(requireContext(), "Ürün başarıyla silindi.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Silme hatası: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun loadDailyCurrencyRates() {

        val url = "https://api.frankfurter.app/latest?from=TRY&to=USD,EUR"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()



        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                Log.e("CURRENCY_API", "API isteği başarısız: ${e.message}")

                requireActivity().runOnUiThread {

                    if (isAdded && _binding != null) {

                        binding.txtCurrency.text = "Hata! Kur API bağlantısı kesik."

                    }

                }

            }



            override fun onResponse(call: Call, response: Response) {

                val body = response.body?.string()

                if (body != null) {

                    try {

                        val json = JSONObject(body)

                        val rates = json.getJSONObject("rates")

                        val usdRate = rates.optDouble("USD", -1.0)

                        val eurRate = rates.optDouble("EUR", -1.0)



                        if (usdRate > 0 && eurRate > 0) {

                            val tryPerUsd = String.format("%.2f", 1 / usdRate)

                            val tryPerEur = String.format("%.2f", 1 / eurRate)

                            requireActivity().runOnUiThread {

                                if (isAdded && _binding != null) {

                                    binding.txtCurrency.text = "1 USD: $tryPerUsd ₺ / 1 EUR: $tryPerEur ₺"

                                }

                            }

                        }

                    } catch (e: Exception) {

                        Log.e("CURRENCY_API", "JSON Hatası: ${e.message}")

                    }

                }

            }

        })

    }



    private fun setupImagePreviewListener() {

        binding.edtProductImageUrl.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                val url = s.toString()

                if (url.startsWith("http")) {

                    Glide.with(requireContext())

                        .load(url)

                        .placeholder(R.drawable.outline_image_24)

                        .error(R.drawable.outline_image_24)

                        .centerCrop()

                        .into(binding.imgProduct)

                } else if (url.isBlank()) {

                    binding.imgProduct.setImageResource(R.drawable.outline_image_24)

                }

            }

            override fun afterTextChanged(s: Editable?) {}

        })}


    private fun performSave() {
        // Mevcut kod
        // ... (Değiştirilmedi)
        val productName = binding.edtProductName.text.toString().trim()
        val productPrice = binding.edtProductPrice.text.toString().toDoubleOrNull()
        val productImageUrl = binding.edtProductImageUrl.text.toString().trim()
        val discountPrice = binding.edtDiscountPrice.text.toString().toDoubleOrNull()
        val stock = binding.edtStock.text.toString().toIntOrNull() ?: 0
        val isDiscounted =
            discountPrice != null && productPrice != null && discountPrice < productPrice

        // Zorunlu alan kontrolü
        if (productName.isBlank() || productPrice == null || productImageUrl.isBlank()) {
            Toast.makeText(
                requireContext(),
                "Ürün adı, fiyatı ve görsel URL'si zorunludur.",
                Toast.LENGTH_LONG
            ).show()
            return
        }


        // Stok 0 ise otomatik satış dışı

        val productToSave = Product(
            name = productName,
            gender = binding.edtProductGender.text.toString().trim(),
            features = binding.edtProductFeatures.text.toString().trim(),
            price = productPrice,
            imageUrl = productImageUrl,
            discountPrice = discountPrice,
            isDiscounted = isDiscounted,
            stock = stock,
            isActive = (stock > 0) // Yeni ürün eklenirken aktiflik kontrolü
        )

        db.collection("products")
            .add(productToSave)
            .addOnSuccessListener {
                clearInputFields()
                sharedViewModel.selectedProduct.value = null // Kaydetme sonrası seçimi temizle
                Toast.makeText(requireContext(), "Ürün başarıyla kaydedildi!", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Kaydetme hatası: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // *** performUpdate() işlevi güncellendi ve kod içine taşındı ***
    private fun performUpdate() {

        val selectedProduct = sharedViewModel.selectedProduct.value

        if (selectedProduct == null) {
            Toast.makeText(requireContext(), "Güncellenecek ürün seçilmedi. Lütfen bir ürün seçin.", Toast.LENGTH_LONG).show()
            return
        }

        val updatedName = binding.edtProductName.text.toString().trim()
        val updatedPrice = binding.edtProductPrice.text.toString().toDoubleOrNull()
        val updatedImageUrl = binding.edtProductImageUrl.text.toString().trim()
        val updatedGender = binding.edtProductGender.text.toString().trim()
        val updatedFeatures = binding.edtProductFeatures.text.toString().trim()
        val updatedDiscountPrice = binding.edtDiscountPrice.text.toString().toDoubleOrNull()
        val updatedStock = binding.edtStock.text.toString().toIntOrNull() ?: 0

        if (updatedName.isBlank() || updatedPrice == null || updatedImageUrl.isBlank()) {
            Toast.makeText(requireContext(), "Zorunlu alanlar (Ad, Fiyat, URL) boş olamaz!", Toast.LENGTH_LONG).show()
            return
        }

        val isDiscounted =
            updatedDiscountPrice != null && updatedPrice != null && updatedDiscountPrice < updatedPrice

        val updatedData = mapOf(
            "name" to updatedName,
            "price" to updatedPrice,
            "imageUrl" to updatedImageUrl,
            "gender" to updatedGender,
            "features" to updatedFeatures,
            "discountPrice" to updatedDiscountPrice,
            "isDiscounted" to isDiscounted,
            "stock" to updatedStock,
            "isActive" to (updatedStock > 0)
        )

        db.collection("products")
            .document(selectedProduct.id)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Ürün başarıyla güncellendi: ${updatedName}", Toast.LENGTH_LONG).show()
                // Başarılı güncelleme sonrası giriş alanlarını temizle ve seçimi kaldır
                clearInputFields()
                sharedViewModel.selectedProduct.value = null
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Güncelleme hatası: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
    // ***************************************************************

    private fun clearInputFields() {
        binding.edtProductName.text.clear()
        binding.edtProductPrice.text.clear()
        binding.edtProductImageUrl.text.clear()
        binding.edtProductGender.text.clear()
        binding.edtProductFeatures.text.clear()
        binding.edtDiscountPrice.text.clear()
        binding.edtStock.text.clear() // Stok alanı da temizlenmeli
        binding.imgProduct.setImageResource(R.drawable.outline_image_24)
    }
}