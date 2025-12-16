package com.example.crafteye8 // kendi paket adın

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController


import android.util.Log
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

import com.google.firebase.firestore.FirebaseFirestore

class A6Fragment : Fragment(R.layout.fragment_a6) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()
    private lateinit var db: FirebaseFirestore

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().navigateUp()
        }


        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter = ProductAdapter(productList, sharedViewModel)
        recyclerView.adapter = adapter

        db = FirebaseFirestore.getInstance()
        loadProductsFromFirebase()
    }

    private fun loadProductsFromFirebase() {
        db.collection("products")
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()
                for (doc in documents) {
                    val product = Product(
                        id = doc.id,
                        name = doc.getString("name") ?: "isimsiz",
                        gender = doc.getString("gender") ?: "",
                        imageUrl = doc.getString("imageUrl") ?: "",
                        price = doc.getDouble("price") ?: 0.0,
                        discountPrice = doc.getDouble("discountPrice"),
                        features = doc.getString("features") ?: "",
                        stock = doc.getLong("stock")?.toInt() ?: 0,
                        isActive = doc.getBoolean("isActive") ?: false,
                        isFavorite = doc.getBoolean("favorite") ?: false,
                        isExpanded = false
                    )

                    // 🔥 Sadece aktif ürünleri listele
                    if (product.isActive) {
                        productList.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("A6Fragment", "Ürünler yüklenemedi", e)
            }
    }
}


