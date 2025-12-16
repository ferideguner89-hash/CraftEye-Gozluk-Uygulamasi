package com.example.crafteye8


import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafteye8.ProductAdapter
import android.widget.ImageView

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
// Diğer importlarınız (Product, ProductAdapter, SharedViewModel)

class GungozFragment : Fragment(R.layout.fragment_gungoz) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()
    private val fullProductList = ArrayList<Product>() // 🔹 tüm ürünleri saklamak için
    private lateinit var db: FirebaseFirestore

    private val args: GungozFragmentArgs by navArgs()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(productList, sharedViewModel)
        recyclerView.adapter = adapter

        fetchProducts(kategori = args.kategori, onlyDiscounted = args.showDiscounted)
    }

    private fun fetchProducts(kategori: String, onlyDiscounted: Boolean) {
        db.collection("products")
            .get()
            .addOnSuccessListener { documents ->

                productList.clear()
                fullProductList.clear()

                for (doc in documents) {
                    val name = doc.getString("name") ?: ""
                    val gender = doc.getString("gender") ?: ""
                    val price = doc.getDouble("price") ?: 0.0
                    val discountPrice = doc.getDouble("discountPrice")

                    // 🔹 Ürünü oluştur
                    val product = Product(
                        name = name,
                        gender = gender,
                        imageUrl = doc.getString("imageUrl") ?: "",
                        price = price,
                        discountPrice = discountPrice,
                        isFavorite = doc.getBoolean("favorite") ?: false,
                        isExpanded = doc.getBoolean("expanded") ?: false,
                        features = doc.getString("features") ?: ""
                    )

                    // 🔹 Filtreleme
                    val matchesCategory = when (kategori.lowercase()) {
                        "çocuk" -> gender.lowercase() == "çocuk"
                        "" -> true // kategori boşsa tüm ürünler
                        else -> name.lowercase().contains(kategori.lowercase())
                    }

                    val matchesDiscount = !onlyDiscounted || (discountPrice != null && discountPrice < price)

                    if (matchesCategory && matchesDiscount) {
                        productList.add(product)
                        fullProductList.add(product)
                    }
                }

                adapter.notifyDataSetChanged()
                if (productList.isEmpty()) {
                    Toast.makeText(requireContext(), "Ürün bulunamadı", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("VeriKontrol", "HATA Firestore okunamadı!", e)
                Toast.makeText(requireContext(), "Hata oluştu!", Toast.LENGTH_SHORT).show()
            }
    }

    // 🔹 Sadece indirimli ürünleri filtrele
    private fun filterDiscountedProducts() {
        val discountedList = fullProductList.filter {
            it.discountPrice != null && it.discountPrice < it.price
        }
        productList.clear()
        productList.addAll(discountedList)
        adapter.notifyDataSetChanged()
    }
}