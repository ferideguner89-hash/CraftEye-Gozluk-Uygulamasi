package com.example.crafteye8
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(
    private val orderList: ArrayList<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtUserDate: TextView = itemView.findViewById(R.id.txtOrderDate)
        val txtProducts: TextView = itemView.findViewById(R.id.txtOrderProducts)
        val txtPrice: TextView = itemView.findViewById(R.id.txtOrderPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]

        holder.txtProducts.text = order.items.joinToString("\n") {
            "${it.name} x${it.count} - ${it.price}₺"
        }



        holder.txtUserDate.text = "Müşteri: ${order.userName} | Tarih: ${order.date}"
        holder.txtPrice.text = "Toplam: ${order.totalPrice} TL"

        holder.itemView.setOnClickListener {
            onItemClick(order)
        }
    }

    override fun getItemCount(): Int = orderList.size
}