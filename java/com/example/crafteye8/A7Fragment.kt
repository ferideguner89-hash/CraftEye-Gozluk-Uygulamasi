package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController


import com.example.crafteye8.databinding.FragmentA7Binding
import com.google.android.play.integrity.internal.b
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class A7Fragment : Fragment() {

    private var _binding: FragmentA7Binding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentA7Binding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
// onViewCreated içinde
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp() // küçük harf yok, U büyük
            // veya direkt A6Fragment'a gitmek istersen:
            // findNavController().navigate(R.id.a6Fragment)
        }



        // Spinner seçenekleri
        val astigmatOptions = arrayOf("Yok", "Var")
        binding.spinnerRightAstigmat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, astigmatOptions)
        binding.spinnerLeftAstigmat.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, astigmatOptions)

        binding.btnSaveGlass.setOnClickListener {
            val rightEye = binding.edtRightEyeNumber.text.toString().trim()
            val leftEye = binding.edtLeftEyeNumber.text.toString().trim()
            val rightAstig = binding.spinnerRightAstigmat.selectedItem.toString()
            val leftAstig = binding.spinnerLeftAstigmat.selectedItem.toString()
            val rightAstigNum = if (binding.edtRightAstigNum.visibility == View.VISIBLE) binding.edtRightAstigNum.text.toString().trim() else ""
            val leftAstigNum = if (binding.edtLeftAstigNum.visibility == View.VISIBLE) binding.edtLeftAstigNum.text.toString().trim() else ""

            if (rightEye.isEmpty() || leftEye.isEmpty()) {
                Toast.makeText(requireContext(), "Lütfen göz numaralarını girin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(requireContext(), "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prescription = hashMapOf(
                "user_id" to userId,
                "right_eye_number" to rightEye.toDouble(),
                "right_astigmat" to rightAstig,
                "right_astig_num" to if (rightAstigNum.isEmpty()) null else rightAstigNum.toDouble(),
                "left_eye_number" to leftEye.toDouble(),
                "left_astigmat" to leftAstig,
                "left_astig_num" to if (leftAstigNum.isEmpty()) null else leftAstigNum.toDouble(),
                "date" to Timestamp.now()
            )

            db.collection("prescriptions")
                .add(prescription)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Reçete kaydedildi", Toast.LENGTH_SHORT).show()
                    // Alanları temizle
                    binding.edtRightEyeNumber.text.clear()
                    binding.edtLeftEyeNumber.text.clear()
                    binding.edtRightAstigNum.text.clear()
                    binding.edtLeftAstigNum.text.clear()
                    binding.spinnerRightAstigmat.setSelection(0)
                    binding.spinnerLeftAstigmat.setSelection(0)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Kaydedilemedi: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}