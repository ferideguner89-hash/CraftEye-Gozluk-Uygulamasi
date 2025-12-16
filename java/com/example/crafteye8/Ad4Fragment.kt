package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.Toast

import com.example.crafteye8.databinding.FragmentAd4Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class Ad4Fragment : Fragment() {


    private var _binding: FragmentAd4Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAd4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        // Şifre sıfırlama
        binding.btnResetPassword.setOnClickListener {
            val newPassword = binding.edtAdminPassword.text.toString().trim()

            if (newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen yeni şifre girin", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val currentUser = auth.currentUser

            if (currentUser != null && currentUser.email == "admin@example.com") {
                currentUser.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Şifre başarıyla güncellendi",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.edtAdminPassword.text.clear()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Şifre güncellenemedi: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Admin olarak giriş yapmanız gerekiyor",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Diğer güncelleme butonunu buraya ekleyebilirsin
        binding.btnUpdateProfile.setOnClickListener {
            val userId = "ADMIN_USER_ID" // Buraya adminin Firestore'daki ID'sini koy
            val newName = binding.edtAdminName.text.toString().trim()
            val newEmail = binding.edtAdminEmail.text.toString().trim()


            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Ad ve e-posta boş olamaz", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val updates = hashMapOf(
                "name" to newName,
                "email" to newEmail,

            )

            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Admin bilgileri güncellendi",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Güncelleme hatası: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }
    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}