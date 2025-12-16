package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentTestsonucBinding


class TestSonucFragment : Fragment() {

    private lateinit var binding: FragmentTestsonucBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestsonucBinding.inflate(inflater, container, false)

        val result = arguments?.getString("result") ?: "Bilinmiyor"
        binding.txtResult.text = "Yüz Tipiniz: $result"

        // Her yüz tipi için öneriler
        val recommendation = when (result) {

            "Kare" -> "Yumuşak hatlı oval ve yuvarlak çerçeveler yüz hatlarını dengeler. Çok kare çerçevelerden kaçın."

            "Oval" -> "Neredeyse tüm çerçeve tiplerini kullanabilirsin! Özellikle kare ve dikdörtgen çerçeveler yüzünü güzel dengeler."

            "Yuvarlak" -> "Keskin hatlı kare, dikdörtgen ve geometrik çerçeveler yüzüne daha çok yakışır."

            "Uzun" -> "Geniş ve yüksek çerçeveler yüzünü daha dengeli gösterir. Dar ve ince çerçevelerden kaçın."

            "Kalp" -> "Aşağı doğru genişleyen çerçeveler, kelebek formu ve yuvarlak çerçeveler yüz hatlarını yumuşatır."

            else -> "Farklı gözlük çerçevelerini deneyebilirsin."
        }

            binding.btnContinue.setOnClickListener {
                findNavController().navigate(R.id.a1Fragment)
            }



        binding.tvRecommendation.text = recommendation
        return binding.root
    }
}






