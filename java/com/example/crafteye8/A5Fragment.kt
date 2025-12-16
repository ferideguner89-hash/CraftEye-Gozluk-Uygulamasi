package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.crafteye8.databinding.FragmentA5Binding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class A5Fragment : Fragment(R.layout.fragment_a5) {

    private var _binding: FragmentA5Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val userId = auth.currentUser?.uid

        // --- Favoriler Butonu ---
        binding.btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.favoritesFragment)
        }
        binding.btnAddress.setOnClickListener {
            findNavController().navigate(R.id.action_a5Fragment_to_addressListFragment)
        }

        binding.btnOrders.setOnClickListener {
            val action = A5FragmentDirections.actionA5FragmentToUserorderFragment()
            findNavController().navigate(action)
        }
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_a5Fragment_to_profileSettingsFragment)
        }


        // --- ÇIKIŞ YAP Butonu ---
        binding.btnLogout.setOnClickListener {
            auth.signOut()                 // Firebase çıkış
            requireActivity().finishAffinity() // Uygulamayı tamamen kapat
        }

        // --- Kullanıcı bilgilerini çekme ---
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val username = doc.getString("name") ?: "Kullanıcı"
                        val email = doc.getString("email") ?: "E-posta yok"

                        binding.textUserName.text = username
                        binding.textUserEmail.text = email
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Kullanıcı bilgileri alınamadı", Toast.LENGTH_SHORT).show()
                }
        } else {
            binding.textUserName.text = "Kullanıcı"
            binding.textUserEmail.text = "E-posta yok"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
