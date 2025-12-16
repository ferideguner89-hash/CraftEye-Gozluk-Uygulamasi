package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentTestkorBinding
class TestkorFragment : Fragment() {

    private lateinit var binding: FragmentTestkorBinding

    // Kullanıcı yanıtlarını saklayacağız
    private var answers = BooleanArray(5) { true } // true = gördü, false = görmedi

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestkorBinding.inflate(inflater, container, false)

        // Örnek: Görsel 1 cevabı
        binding.rbQ1Option1.setOnClickListener { answers[0] = true }
        binding.rbQ1Option2.setOnClickListener { answers[0] = false }

        // Görsel 2 cevabı
        binding.rbQ2Option1.setOnClickListener { answers[1] = true }
        binding.rbQ2Option2.setOnClickListener { answers[1] = false }

        // Görsel 3 cevabı
        binding.rbQ3Option1.setOnClickListener { answers[2] = true }
        binding.rbQ3Option2.setOnClickListener { answers[2] = false }

        // Görsel 4 cevabı
        binding.rbQ4Option1.setOnClickListener { answers[3] = true }
        binding.rbQ4Option2.setOnClickListener { answers[3] = false }

        // Görsel 5 cevabı
        binding.rbQ5Option1.setOnClickListener { answers[4] = true }
        binding.rbQ5Option2.setOnClickListener { answers[4] = false }

        // Finish butonuna basınca sonuçları hesapla ve KorsonucFragment’a geç
        binding.btnFinishTest.setOnClickListener {
            val result = calculateResult(answers)

            // Bundle ile sonucu gönder
            val bundle = bundleOf("result" to result)
            findNavController().navigate(R.id.action_testkorFragment_to_korsonucFragment, bundle)
        }

        return binding.root
    }
}
private fun calculateResult(answers: BooleanArray): String {
    // Görsel 1
    if (!answers[0]) return "Kırmızı Renk Körlüğü"

    // Görsel 2
    if (!answers[1]) return "Kırmızı-Yeşil Renk Körlüğü"

    // Görsel 3
    if (!answers[2]) return "Hafif Renk Körlüğü"

    // Görsel 4
    if (!answers[3]) return "Mavi-Sarı Renk Körlüğü"

    // Görsel 5
    if (!answers[4]) return "Normal Görme"

    return "Normal Görme"
}
