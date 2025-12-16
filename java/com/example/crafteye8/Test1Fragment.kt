package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentTest1Binding



    class Test1Fragment : Fragment() {

        private lateinit var binding: FragmentTest1Binding

        private var questionIndex = 0
        private var scoreOval = 0
        private var scoreRound = 0
        private var scoreSquare = 0
        private var scoreLong = 0
        private var scoreHeart = 0

        private val questions = listOf(
            Pair("Yüz genişliğinizi nasıl tanımlarsınız?", listOf("Dar", "Orta", "Geniş")),
            Pair("Çene hattınız nasıl?", listOf("Yuvarlak", "Sivri", "Kare")),
            Pair("En geniş bölgeniz hangisi?", listOf("Alın", "Elmacık kemikleri", "Çene", "Hepsi eşit")),
            Pair("Yüz uzunluğu?", listOf("Daha uzun", "Daha geniş", "Eşit")),
            Pair("Yüz hatları?", listOf("Yumuşak", "Keskin", "Karışık")),
            Pair("Kendinizi hangisine yakın görüyorsunuz?", listOf("Oval", "Yuvarlak", "Kare", "Uzun", "Kalp"))
        )

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            binding = FragmentTest1Binding.inflate(inflater, container, false)

            loadQuestion()

            binding.btnOption1.setOnClickListener { answerSelected(0) }
            binding.btnOption2.setOnClickListener { answerSelected(1) }
            binding.btnOption3.setOnClickListener { answerSelected(2) }
            binding.btnOption4.setOnClickListener { answerSelected(3) }

            return binding.root
        }

        private fun loadQuestion() {
            val (question, options) = questions[questionIndex]

            binding.txtQuestion.text = question
            binding.btnOption1.text = options.getOrNull(0) ?: ""
            binding.btnOption2.text = options.getOrNull(1) ?: ""
            binding.btnOption3.text = options.getOrNull(2) ?: ""
            binding.btnOption4.text = options.getOrNull(3) ?: ""

            binding.btnOption3.visibility = if (options.size >= 3) View.VISIBLE else View.GONE
            binding.btnOption4.visibility = if (options.size >= 4) View.VISIBLE else View.GONE
        }

        private fun answerSelected(option: Int) {
            // Skor güncelleme
            when (questionIndex) {
                0 -> when (option) { 0 -> scoreLong++; 1 -> scoreOval++; 2 -> scoreRound++ }
                1 -> when (option) { 0 -> scoreRound++; 1 -> scoreHeart++; 2 -> scoreSquare++ }
                2 -> when (option) { 0 -> scoreHeart++; 1 -> scoreOval++; 2 -> scoreSquare++; 3 -> scoreRound++ }
                3 -> when (option) { 0 -> scoreHeart++; 1 -> scoreSquare++; 2 -> scoreOval++ }
                4 -> when (option) { 0 -> scoreLong++; 1 -> scoreRound++; 2 -> scoreOval++ }
                5 -> when (option) { 0 -> scoreRound++; 1 -> scoreSquare++; 2 -> scoreOval++ }
                6 -> when (option) { 0 -> scoreRound++; 1 -> scoreHeart++; 2 -> scoreSquare++ }
                7 -> when (option) {
                    0 -> scoreOval += 3
                    1 -> scoreRound += 3
                    2 -> scoreSquare += 3
                    3 -> scoreLong += 3
                    4 -> scoreHeart += 3
                }
            }

            questionIndex++

            if (questionIndex < questions.size) {
                loadQuestion()
            } else {
                goToResult()
            }
        }

        private fun goToResult() {
            val result = mapOf(
                "Oval" to scoreOval,
                "Yuvarlak" to scoreRound,
                "Kare" to scoreSquare,
                "Uzun" to scoreLong,
                "Kalp" to scoreHeart
            ).maxBy { it.value }.key

            // Sonucu Bundle ile TestSonucFragment'a gönder
            val bundle = Bundle().apply {
                putString("result", result)
            }

            // Safe Args yok → id ile navigate
            findNavController().navigate(R.id.test1denson, bundle)
        }
    }
