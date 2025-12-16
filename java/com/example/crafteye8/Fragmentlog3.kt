package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.crafteye8.databinding.FragmentFragmentlog3Binding
import com.google.firebase.auth.FirebaseAuth



class Fragmentlog3 : Fragment() {
    private var _binding: FragmentFragmentlog3Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFragmentlog3Binding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        // Şifre sıfırlama butonuna tıklama
        binding.sifirbutton.setOnClickListener {

            val email = binding.editTextText3.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "E-posta giriniz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(requireContext(), "Sıfırlama linki gönderildi", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            task.exception?.message ?: "Hata oluştu",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}