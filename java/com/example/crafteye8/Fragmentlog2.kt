package com.example.crafteye8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentFragmentlog2Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragmentlog2 : Fragment() {

        private var _binding: FragmentFragmentlog2Binding? = null
        private val binding get() = _binding!!

        private lateinit var auth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = FragmentFragmentlog2Binding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            auth = FirebaseAuth.getInstance()
            db = FirebaseFirestore.getInstance()

            binding.kytbutton.setOnClickListener {

                val name = binding.editTextText4.text.toString().trim()
                val email = binding.editTextText5.text.toString().trim()
                val password = binding.editTextText6.text.toString().trim()
                val confirmPassword = binding.editTextText7.text.toString().trim()

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), "Tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (password != confirmPassword) {
                    Toast.makeText(requireContext(), "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                            val user = hashMapOf(
                                "name" to name,
                                "email" to email
                            )

                            db.collection("users").document(userId)
                                .set(user)
                                .addOnSuccessListener {

                                    Toast.makeText(
                                        requireContext(),
                                        "Kayıt başarılı!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // 🔹 EditText'leri temizle
                                    binding.editTextText4.text?.clear()
                                    binding.editTextText5.text?.clear()
                                    binding.editTextText6.text?.clear()
                                    binding.editTextText7.text?.clear()

                                    // 🔹 Giriş ekranına git
                                    findNavController().navigate(
                                        R.id.action_fragmentlog2_to_fragmentlog1
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        requireContext(),
                                        "Veri kaydı başarısız: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Hata: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }
