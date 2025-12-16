package com.example.crafteye8
import android.view.LayoutInflater
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.crafteye8.R

class UserorderFragment : Fragment(R.layout.fragment_user_order) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter
    private val orderList = ArrayList<Order>()

    private val db = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerOrders)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = OrderAdapter(orderList) { selectedOrder ->
            // Tıklayınca detay işlemi ekleyebilirsin
        }
        recyclerView.adapter = adapter

        loadUserOrders()
    }

    private fun loadUserOrders() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) return

        // Kullanıcı adını çek
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { doc ->
                val username = doc.getString("name") ?: return@addOnSuccessListener

                // Kullanıcının siparişlerini çek
                db.collection("orders")
                    .whereEqualTo("userName", username)
                    .get()
                    .addOnSuccessListener { ordersSnapshot ->
                        orderList.clear()

                        if (ordersSnapshot.isEmpty) {
                            adapter.notifyDataSetChanged()
                            return@addOnSuccessListener
                       }

                        // Her sipariş için items koleksiyonunu çek
                        for (orderDoc in ordersSnapshot) {
                            val order = orderDoc.toObject(Order::class.java)

                            db.collection("orders")
                                .document(orderDoc.id)
                                .collection("items")
                                .get()
                                .addOnSuccessListener { itemsSnapshot ->
                                    val itemsList = itemsSnapshot.map { itemDoc ->
                                        itemDoc.toObject(Item::class.java)
                                    }
                                    order.items = itemsList
                                    orderList.add(order)
                                    adapter.notifyDataSetChanged()
                                }
                        }
                    }
            }
    }
}