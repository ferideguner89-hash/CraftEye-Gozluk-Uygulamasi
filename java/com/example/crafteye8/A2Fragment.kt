package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentA2Binding


class A2Fragment : Fragment() {

    private lateinit var tasarim: FragmentA2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        tasarim = FragmentA2Binding.inflate(inflater, container, false)

        tasarim.imageView10.setOnClickListener {
            findNavController().navigate(R.id.a6Fragment)
        }

        tasarim.imageView8.setOnClickListener {
            findNavController().navigate(R.id.action_a2Fragment_to_a7Fragment2)
        }

        tasarim.imageView.setOnClickListener {
            val action = A2FragmentDirections.actionA2FragmentToGungozFragment(
                kategori = "Güneş Gözlüğü",
                showDiscounted = false
            )
            findNavController().navigate(action)
        }

        tasarim.imageView2.setOnClickListener {
            val action = A2FragmentDirections.actionA2FragmentToGungozFragment(
                kategori = "Optik Gözlüğü",
                showDiscounted = false
            )
            findNavController().navigate(action)
        }

        tasarim.imageView3.setOnClickListener {
            val action = A2FragmentDirections.actionA2FragmentToGungozFragment(
                kategori = "Çocuk",
                showDiscounted = false
            )
            findNavController().navigate(action)
        }

        tasarim.imageView7.setOnClickListener {
            val action = A2FragmentDirections.actionA2FragmentToGungozFragment(
                kategori = "Spor Gözlüğü",
                showDiscounted = false
            )
            findNavController().navigate(action)
        }

        return tasarim.root
    }
}