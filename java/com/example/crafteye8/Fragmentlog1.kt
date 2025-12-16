package com.example.crafteye8

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController

import com.example.crafteye8.databinding.FragmentFragmentlog1Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Fragmentlog1 : Fragment() {

    private var _binding: FragmentFragmentlog1Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val adminEmail = "admin@example.com"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFragmentlog1Binding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // "Kayıt ol" TextView
        binding.textLogin.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentlog1_to_fragmentlog2)
        }

        // "Şifremi unuttum" TextView
        binding.textView3.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentlog1_to_fragmentlog3)
        }

        // Giriş butonu
        binding.buttonlog1.setOnClickListener {
            val email = binding.editUser.text.toString().trim()
            val password = binding.editPass.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Email ve şifre giriniz", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase login
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        if (user != null) {
                            val userId = user.uid

                            // Firestore’dan kullanıcı bilgilerini al
                            db.collection("users").document(userId).get()
                                .addOnSuccessListener { document ->
                                    val isActive = document.getBoolean("isActive") ?: true
                                    val username = document.getString("ad") ?: "Kullanıcı"

                                    if (!isActive) {
                                        // Hesap pasif → popup göster
                                        AlertDialog.Builder(requireContext())
                                            .setTitle("Hesabınız Pasif")
                                            .setMessage("Hesabınız pasif. Tekrar aktifleştirmek ister misiniz?")
                                            .setPositiveButton("Evet") { _, _ ->
                                                // Hesabı tekrar aktif et
                                                db.collection("users").document(userId)
                                                    .update("isActive", true)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(requireContext(), "Hesabınız tekrar aktif edildi!", Toast.LENGTH_SHORT).show()
                                                        // Ana ekrana yönlendir
                                                        val intent = Intent(requireActivity(), MainActivity::class.java)
                                                        intent.putExtra("USERNAME", username)
                                                        startActivity(intent)
                                                        requireActivity().finish()
                                                    }
                                            }
                                            .setNegativeButton("Hayır") { dialog, _ ->
                                                dialog.dismiss()
                                                auth.signOut() // giriş iptal
                                            }
                                            .show()
                                    } else {
                                        // Admin kontrolü
                                        if (user.email == adminEmail) {
                                            val intent = Intent(requireActivity(), AdminActivity::class.java)
                                            startActivity(intent)
                                            requireActivity().finish()
                                            return@addOnSuccessListener
                                        }

                                        // Normal kullanıcı → giriş başarılı
                                        val intent = Intent(requireActivity(), MainActivity::class.java)
                                        intent.putExtra("USERNAME", username)
                                        startActivity(intent)
                                        requireActivity().finish()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Kullanıcı adı alınamadı", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Email veya şifre hatalı", Toast.LENGTH_SHORT).show()
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