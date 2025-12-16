package com.example.crafteye8

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Button
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Locale


class Ad3Fragment : Fragment(R.layout.fragment_ad3) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    private lateinit var firestore: FirebaseFirestore

    private lateinit var edtUser: EditText
    private lateinit var edtTotal: EditText
    private lateinit var edtDate: EditText
    private lateinit var btnApprove: Button

    private val orderList = ArrayList<Order>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        recyclerView = view.findViewById(R.id.recyclerOrders)
        edtUser = view.findViewById(R.id.edtOrderUser)
        edtTotal = view.findViewById(R.id.edtOrderTotal)
        edtDate = view.findViewById(R.id.edtOrderDate)
        btnApprove = view.findViewById(R.id.btnApprove)

        adapter = OrderAdapter(orderList) { selectedOrder ->
            showOrderDetails(selectedOrder)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadOrders() // Siparişleri yükle

        btnApprove.setOnClickListener {
            val selectedUserName = edtUser.text.toString()
            if (selectedUserName.isEmpty()) {
                Toast.makeText(requireContext(), "Önce bir sipariş seçin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tüm siparişleri alıp kullanıcı adına bakmadan işaretleme
            firestore.collection("orders")
                .get()
                .addOnSuccessListener { docs ->
                    if (docs.isEmpty) {
                        Toast.makeText(requireContext(), "Sipariş bulunamadı!", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    Toast.makeText(requireContext(), "Sipariş onaylandı!", Toast.LENGTH_SHORT).show()
                    loadOrders() // Listeyi yenile
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Onay başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadOrders() {
        firestore.collection("orders")
            .get()
            .addOnSuccessListener { result ->
                orderList.clear()
                for (doc in result) {
                    val order = doc.toObject(Order::class.java)
                    val orderId = doc.id

                    // Alt collection items
                    firestore.collection("orders")
                        .document(orderId)
                        .collection("items")
                        .get()
                        .addOnSuccessListener { itemsSnapshot ->
                            val itemsList = itemsSnapshot.map { it.toObject(Item::class.java) }
                            order.items = itemsList
                            adapter.notifyDataSetChanged()
                        }

                    orderList.add(order)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Siparişler alınamadı!", Toast.LENGTH_SHORT).show()
                Log.e("Ad3Fragment", "Firestore fetch failed", e)
            }
    }


    private fun showOrderDetails(order: Order) {
        edtUser.setText(order.userName)
        edtTotal.setText(order.totalPrice.toString())
        edtDate.setText(order.date)
    }
}