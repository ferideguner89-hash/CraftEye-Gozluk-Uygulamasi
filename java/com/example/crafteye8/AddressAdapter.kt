package com.example.crafteye8.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crafteye8.R
import com.example.crafteye8.model.Address

class AddressAdapter(private val addressList: List<Address>) :
    RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    inner class AddressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txtAddressTitle)
        val txtDetail: TextView = itemView.findViewById(R.id.txtAddressDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_adress, parent, false)
        return AddressViewHolder(view)
    }

    override fun getItemCount(): Int = addressList.size

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.txtTitle.text = address.title
        holder.txtDetail.text = address.detail
    }
}
