package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentA3Binding


class A3Fragment : Fragment() {


    private lateinit var tasarim: FragmentA3Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        tasarim = FragmentA3Binding.inflate(inflater, container, false)

        tasarim.yuztest.setOnClickListener {
            // Safe Args yok → sadece action id ile navigate
            findNavController().navigate(R.id.a3dentest1)
        }
        tasarim.kortest.setOnClickListener {
            findNavController().navigate(R.id.action_a3Fragment_to_testkorFragment)
        }

        return tasarim.root
    }



    }




