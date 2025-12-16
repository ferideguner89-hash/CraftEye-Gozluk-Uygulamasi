package com.example.crafteye8

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.crafteye8.databinding.FragmentProfileSettingsBinding
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileSettingsFragment : Fragment() {

    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val currentUser get() = auth.currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        // Mevcut kullanıcı bilgilerini doldur
        currentUser?.let { user ->
            binding.edtName.setText(user.displayName) // FirebaseUser displayName varsa
            binding.edtEmail.setText(user.email)
        }
// Üyeliği Pasifleştir butonu
        binding.btnDeactivate.setOnClickListener {
            currentUser?.let { user ->
                db.collection("users").document(user.uid)
                    .update("isActive", false)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Üyeliğiniz pasifleştirildi.", Toast.LENGTH_SHORT).show()
                        auth.signOut() // opsiyonel: kullanıcıyı çıkış yaptır
                        // İstersen login ekranına yönlendirebilirsin
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Hata: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }

        // Profili güncelle butonu
        binding.btnUpdateProfile.setOnClickListener {
            val newName = binding.edtName.text.toString().trim()
            val newEmail = binding.edtEmail.text.toString().trim()
            val oldPassword = binding.edtOldPassword.text.toString().trim()
            val newPassword = binding.edtPassword.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(requireContext(), "Ad ve e-posta boş olamaz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            currentUser?.let { user ->

                // --- Firestore'da adı güncelle ---
                db.collection("users").document(user.uid)
                    .update("name", newName)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Ad başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Ad güncelleme hatası: ${it.message}", Toast.LENGTH_LONG).show()
                    }

                // --- Firebase Authentication: E-posta güncelle ---
                if (newEmail != user.email) {
                    user.updateEmail(newEmail)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "E-posta başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "E-posta güncelleme hatası: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                }

                // --- Firebase Authentication: Şifre güncelle ---

                if (newPassword.isNotEmpty()) {

                    if (oldPassword.isEmpty()) {
                        Toast.makeText(requireContext(), "Eski şifreyi girin", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }

                    val email = user.email ?: return@setOnClickListener

                    val credential = EmailAuthProvider.getCredential(email, oldPassword)

                    user.reauthenticate(credential)
                        .addOnSuccessListener {

                            user.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Şifre başarıyla güncellendi", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        requireContext(),
                                        "Şifre güncelleme hatası: ${it.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Eski şifre yanlış", Toast.LENGTH_LONG).show()
                        }
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
