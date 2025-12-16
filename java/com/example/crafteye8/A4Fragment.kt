package com.example.crafteye8

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crafteye8.databinding.FragmentA4Binding
import androidx.recyclerview.widget.RecyclerView
import com.example.crafteye8.SharedViewModel
import com.example.crafteye8.CartAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.util.Date
import java.util.Locale
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

class A4Fragment : Fragment(R.layout.fragment_a4) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CartAdapter
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnCheckout: Button

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerCart)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tvTotalAmount = view.findViewById(R.id.tvTotalAmount)
        btnCheckout = view.findViewById(R.id.btnCheckout)


        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            findNavController().navigate(R.id.a6Fragment)
        }


        adapter = CartAdapter(sharedViewModel)
        recyclerView.adapter = adapter

        // Toplam tutarı güncelle
        sharedViewModel.cartItems.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list.toMutableList())

            val total = list.sumOf { it.price.toDouble() * it.count }
            tvTotalAmount.text = "%.2f ₺".format(total)
        }

        // Ödeme butonu → Siparişi admin ekranına gönder
        btnCheckout.setOnClickListener {
            val cartList = sharedViewModel.cartItems.value

            if (cartList.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Sepet boş!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 1. Butonu kilitle (Çift tıklamayı engeller)
            btnCheckout.isEnabled = false

            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val firestore = FirebaseFirestore.getInstance()

            if (userId == null) {
                Toast.makeText(requireContext(), "Giriş yapmadınız!", Toast.LENGTH_SHORT).show()
                btnCheckout.isEnabled = true // İşlem bitti, butonu aç
                return@setOnClickListener
            }
            // Önce Firestore'dan kullanıcı adını çekiyoruz
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { doc ->

                    val userName = doc.getString("name") ?: "Bilinmiyor"

                    // Toplam tutarı hesapla
                    val totalText = tvTotalAmount.text.toString().replace("₺", "").trim()
                    val totalDouble = totalText.toDoubleOrNull()
                        ?: cartList.sumOf { it.price.toDouble() * it.count }

                    // Tarih oluştur
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val dateStr = sdf.format(Date())
                    // Ana Order nesnesi (SADECE temel bilgiler)
                    val order = Order(
                        userName = userName,
                        totalPrice = totalDouble,
                        date = dateStr
                        // Ekstra olarak kullanıcı ID'sini de eklemeniz şiddetle tavsiye edilir
                    )
                    // 2. Ana Sipariş Kaydını Oluşturma
                    firestore.collection("orders")
                        .add(order)
                        .addOnSuccessListener { orderRef ->
                            // 3. Ürün Detayları İçin Alt Koleksiyon Yazma (Batch Write)
                            // Bu, orders/{orderId}/items altında her bir ürünü kaydeder.

                            val batch = firestore.batch()

                            cartList.forEach { cartItem ->
                                // Kaydedilecek ürün detayları haritası
                                val orderItemMap = mapOf( // Varsayım: CartItem'ın ID alanı var
                                    "name" to cartItem.name,
                                    "price" to cartItem.price.toDouble(),
                                    "count" to cartItem.count
                                )
                                // Alt koleksiyon referansı: orders/{orderId}/items/{newItemId}
                                val itemRef = orderRef.collection("items").document()
                                batch.set(itemRef, orderItemMap)
                            }
                            // 4. Batch İşlemini Gerçekleştirme
                            batch.commit()
                                .addOnSuccessListener {
                                    Toast.makeText(requireContext(), "Sipariş kaydedildi ve ürünler eklendi!", Toast.LENGTH_LONG).show()
                                    sharedViewModel.clearCart() // Sepeti temizle
                                }
                                .addOnFailureListener { e ->
                                    // Batch hatası (Ürünler kaydedilemezse)
                                    Toast.makeText(requireContext(), "Hata: Ürün detayları kaydedilemedi: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                                .addOnCompleteListener {
                                    btnCheckout.isEnabled = true // İşlem bitti, butonu aç
                                }

                        }
                        .addOnFailureListener { e ->
                            // Ana Order kaydı hatası
                            Toast.makeText(requireContext(), "Hata: Ana sipariş kaydedilemedi: ${e.message}", Toast.LENGTH_LONG).show()
                            btnCheckout.isEnabled = true // İşlem bitti, butonu aç
                        }

                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Kullanıcı bilgisi alınamadı!", Toast.LENGTH_SHORT).show()
                    btnCheckout.isEnabled = true // İşlem bitti, butonu aç
                }
        }}}