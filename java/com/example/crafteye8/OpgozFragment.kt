package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafteye8.ProductAdapter
import com.example.crafteye8.Product
import com.google.firebase.firestore.FirebaseFirestore

class OpgozFragment : Fragment(R.layout.fragment_opgoz2) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val productList = ArrayList<Product>()
    private lateinit var db: FirebaseFirestore

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.recyclerViewOpgoz)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(productList, sharedViewModel)
        recyclerView.adapter = adapter

        fetchOptikGozluk()
    }

    private fun fetchOptikGozluk() {
        db.collection("Products")
            .get()
            .addOnSuccessListener { documents ->
                productList.clear()

                for (doc in documents) {
                    val product = doc.toObject(Product::class.java)
                    val name = product.name?.lowercase() ?: ""

                    if (name.contains("optik gözlük")) {
                        productList.add(product)
                    }
                }

                Log.d("OpgozFragment", "Bulunan optik ürün sayısı: ${productList.size}")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.e("OpgozFragment", "Optik gözlükler yüklenemedi", it)
            }
    }
}
