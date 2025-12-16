package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentKorsonucBinding


class KorsonucFragment : Fragment() {
    private lateinit var binding: FragmentKorsonucBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {




        binding = FragmentKorsonucBinding.inflate(inflater, container, false)


        binding.btnBackHome.setOnClickListener {
            findNavController().navigate(R.id.a1Fragment)
        }

        // TestKorFragment'tan gelen sonucu al
        val result = arguments?.getString("result") ?: "Bilinmiyor"

        // Sonucu TextView'a yaz
        binding.tvGeneralComment.text = result

        // Tamam butonuna basınca geri veya ana sayfaya dönebilirsin


        return binding.root
    }
}
