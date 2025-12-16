package com.example.crafteye8
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CartAdapter(
    private val viewModel: SharedViewModel
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartList = ArrayList<CartItem>()

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.imgCartProduct)
        val productName: TextView = itemView.findViewById(R.id.txtCartProductName)
        val productPrice: TextView = itemView.findViewById(R.id.txtCartProductPrice)
        val productCount: TextView = itemView.findViewById(R.id.txtCartCount)

        val btnPlus: ImageView = itemView.findViewById(R.id.btnPlus)
        val btnMinus: ImageView = itemView.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        // Ürün adı
        holder.productName.text = item.name

        // Fiyat - Double olduğundan yazım düzgün
        holder.productPrice.text = "${item.price} ₺"

        // Adet
        holder.productCount.text = item.count.toString()

        // Resim yükleme
        Glide.with(holder.itemView.context)
            .load(item.imageUrl)
            .placeholder(R.drawable.mav)
            .error(R.drawable.mav)
            .into(holder.productImage)

        // + butonu → artır
        holder.btnPlus.setOnClickListener {
            viewModel.increaseQuantity(item)
        }

        // – butonu → azalt / sil
        holder.btnMinus.setOnClickListener {
            viewModel.decreaseQuantity(item)
        }
    }

    override fun getItemCount(): Int = cartList.size

    /** SharedViewModel verisini adapter’a aktarma */
    fun submitList(newList: MutableList<CartItem>) {
        cartList.clear()
        cartList.addAll(newList)
        notifyDataSetChanged()
    }
}


