package com.example.crafteye8

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class UserAdapter(
    private var users: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(android.R.id.text1)
        val txtEmail: TextView = itemView.findViewById(android.R.id.text2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LinearLayout(parent.context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            isClickable = true
            isFocusable = true

            addView(TextView(context).apply {
                id = android.R.id.text1
                textSize = 16f
            })

            addView(TextView(context).apply {
                id = android.R.id.text2
                textSize = 14f
            })
        }
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]

        holder.txtName.text = user.name
        holder.txtEmail.text = user.email

        // ✅ NORMAL TIKLAMA → EditText'leri doldurur
        holder.itemView.setOnClickListener {
            onItemClick(user)
        }
        // ✅ UZUN BASMA → Aktif / Pasif
        holder.itemView.setOnLongClickListener {
            val action = if (user.isActive) "pasifleştirmek" else "aktifleştirmek"

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Hesap Durumu")
                .setMessage("${user.name} kullanıcısını $action istiyor musunuz?")
                .setPositiveButton("Evet") { _, _ ->
                    val newStatus = !user.isActive
                    user.isActive = newStatus

                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.id)
                        .update("isActive", newStatus)

                    val statusText = if (newStatus) "AKTİF" else "PASİF"
                    Toast.makeText(
                        holder.itemView.context,
                        "${user.name} artık $statusText",
                        Toast.LENGTH_SHORT
                    ).show()

                    notifyItemChanged(position)
                }
                .setNegativeButton("Hayır", null)
                .show()

            true
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateList(newList: List<User>) {
        users = newList
        notifyDataSetChanged()
    }
}
