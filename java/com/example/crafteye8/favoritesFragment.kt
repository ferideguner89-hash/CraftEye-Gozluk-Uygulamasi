package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.crafteye8.Product
import com.example.crafteye8.ProductAdapter
import com.example.crafteye8.SharedViewModel


class favoritesFragment : Fragment

    (R.layout.fragment_favorites) {

        private lateinit var recyclerView: RecyclerView
        private lateinit var adapter: ProductAdapter

        private val sharedViewModel: SharedViewModel by activityViewModels()

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            recyclerView = view.findViewById(R.id.recyclerFavorites)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // ViewModel'daki gerçek favori listesi
            val favoriteList = sharedViewModel.favoriteList

            // Adapter → Favori ekranında olduğumuzu söylüyoruz
            adapter = ProductAdapter(favoriteList, sharedViewModel, isFavoriteScreen = true)

            // Favoriden silme
            adapter.onDeleteFavorite = { product ->
                sharedViewModel.removeFavorite(product)
                adapter.notifyDataSetChanged()
            }

            recyclerView.adapter = adapter

            val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
            btnBack.setOnClickListener {
                findNavController().navigateUp() // NavController kullanıyorsan
                // veya klasik:
                // requireActi





        }




    }}