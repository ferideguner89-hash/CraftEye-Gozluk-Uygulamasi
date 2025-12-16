package com.example.crafteye8

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentA1Binding


class A1Fragment : Fragment() {
    private lateinit var tasarim: FragmentA1Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tasarim = FragmentA1Binding.inflate(inflater, container, false)
        return tasarim.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ImageView5 tıklaması → GungozFragment'e geçiş
        tasarim.imageView5.setOnClickListener {
            // GungozFragment'e sadece indirimli ürünleri göstermek için showDiscounted = true gönderiyoruz
            val action = A1FragmentDirections
                .actionA1FragmentToGungozFragment(
                    kategori = "",          // Kategori boş bırakılabilir, çünkü indirim filtresi öncelikli
                    showDiscounted = true   // Sadece indirimli ürünleri göster
                )
            findNavController().navigate(action)
        }


    }}




