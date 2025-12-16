package com.example.crafteye8 // kendi paket adını yaz

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class ProductAdapter(
    private val productList: ArrayList<Product>,
    private val sharedViewModel: SharedViewModel,
    private val isFavoriteScreen: Boolean = false,
    private val isAdmin: Boolean = false, // Admin yetkisi
    private val onDeleteProduct: ((Product) -> Unit)? = null // Silme callback
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    var onDeleteFavorite: ((Product) -> Unit)? = null

    // ⭐ DÜZELTME: Firebase Firestore nesnesini sadece bir kere oluşturup kullanıyoruz
    private val db = FirebaseFirestore.getInstance() // Firebase KTX yerine standart yöntem

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.imgProduct)
        val productName: TextView = itemView.findViewById(R.id.txtProductName)
        val productGender: TextView = itemView.findViewById(R.id.txtProductGender)
        val productPrice: TextView = itemView.findViewById(R.id.txtProductPrice)
        val productDiscountPrice: TextView = itemView.findViewById(R.id.txtProductDiscountPrice)
        val favoriteBtn: ImageView = itemView.findViewById(R.id.btnFavorite)
        val addToCartBtn: Button = itemView.findViewById(R.id.btnAddToCart)
        val layoutDetails: LinearLayout = itemView.findViewById(R.id.layoutDetails)
        val productFeatures: TextView = itemView.findViewById(R.id.txtProductFeatures)
        val btnFavoriteRemove: ImageView = itemView.findViewById(R.id.btnFavoriteRemove)

        // ⭐ DÜZELTME 1: SwitchCompat görünümünü buraya ekledik (XML ID'sini kontrol edin!)
        val switchActive: SwitchCompat = itemView.findViewById(R.id.switchActive) // Opsiyonel: Yönetim görünümlerini içeren bir layout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // --- Ürün bilgilerini göster ---
        holder.productName.text = product.name
        holder.productGender.text = product.gender
        holder.productFeatures.text = product.features
        holder.layoutDetails.visibility = if (product.isExpanded) View.VISIBLE else View.GONE

        Glide.with(holder.itemView.context)
            .load(product.imageUrl)
            .placeholder(R.drawable.mav)
            .error(R.drawable.mav)
            .into(holder.productImage)

        // =======================================================
        // 🚀 ADMIN PANELİ: SATIŞTA / SATIŞTA DEĞİL ANAHTARI
        // =======================================================
        if (isAdmin) {
            holder.switchActive.visibility = View.VISIBLE

            // Eski listener'ı temizle
            holder.switchActive.setOnCheckedChangeListener(null)

            // Stok 0 ise zorla pasif yap, aksi halde Firestore'daki isActive durumunu kullan
            holder.switchActive.isChecked = if (product.stock == 0) {
                product.isActive = false
                false
            } else {
                product.isActive
            }

            // Listener ekle
            holder.switchActive.setOnCheckedChangeListener { _, isChecked ->
                // Stok 0 iken aktif yapılmasını engelle
                if (product.stock == 0 && isChecked) {
                    Toast.makeText(
                        holder.itemView.context,
                        "Stok 0, ürünü aktif yapamazsınız!",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.switchActive.isChecked = false
                    return@setOnCheckedChangeListener
                }

                // Local veri güncelle
                product.isActive = isChecked

                // Firebase güncelle
                updateProductStatusInDatabase(product.id, isChecked)

                val statusText = if (isChecked) "AKTİF" else "PASİF"
                Toast.makeText(
                    holder.itemView.context,
                    "${product.name} ürünü $statusText yapıldı. Stok: ${product.stock}",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            holder.switchActive.visibility = View.GONE
            holder.switchActive.setOnCheckedChangeListener(null)
        }

// ... (Diğer kodlar) ...

        // --- Fiyat Mantığı (Mevcut kodunuz) ---
        if (product.discountPrice != null && product.discountPrice < product.price) {
            holder.productPrice.text = "${product.price} TL"
            holder.productPrice.setTextColor(Color.GRAY)
            holder.productPrice.paintFlags =
                holder.productPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            holder.productDiscountPrice.text = "${product.discountPrice} TL"
            holder.productDiscountPrice.setTextColor(Color.RED)
            holder.productDiscountPrice.visibility = View.VISIBLE
        } else {
            holder.productPrice.text = "${product.price} TL"
            holder.productPrice.paintFlags =
                holder.productPrice.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.productPrice.setTextColor(Color.BLACK)
            holder.productDiscountPrice.visibility = View.GONE
        }

        // --- Favori Ekranı (Mevcut kodunuz) ---
        // ... (Favori/Sepet/Diğer mantıklar burayı takip eder) ...

        // --- Favori Ekranı ---
        if (!isFavoriteScreen) {
            holder.favoriteBtn.visibility = View.VISIBLE
            holder.btnFavoriteRemove.visibility = View.GONE

            holder.favoriteBtn.setImageResource(
                if (product.isFavorite) R.drawable.baseline_favorite_24
                else R.drawable.outline_favorite_24
            )

            holder.favoriteBtn.setOnClickListener {
                product.isFavorite = !product.isFavorite
                if (product.isFavorite) sharedViewModel.favoriteList.add(product)
                else sharedViewModel.favoriteList.remove(product)
                notifyItemChanged(position)
            }
        } else {
            holder.favoriteBtn.visibility = View.GONE
            holder.btnFavoriteRemove.visibility = View.VISIBLE

            holder.btnFavoriteRemove.setOnClickListener {
                onDeleteFavorite?.invoke(product)
            }
        }

        // --- Ürün seç + detay aç/kapa ---
        holder.itemView.setOnClickListener {

            // 🔥 GÜNCELLENECEK ÜRÜNÜ SEÇ (Sadece Admin ise)
            if (isAdmin) {
                sharedViewModel.selectedProduct.value = product
            }

            product.isExpanded = !product.isExpanded
            notifyItemChanged(position)
        }


        // --- Sepete ekle ---
        // NOT: isChecked (isActive) kontrolü buraya eklenebilir. Müşteriler pasif ürünleri sepete ekleyemesin.
        holder.addToCartBtn.isEnabled = product.isActive // Sadece aktif ürünler sepete eklensin
        holder.addToCartBtn.setOnClickListener {
            // ... (Sepete ekleme mantığı) ...
            if (product.isActive) {
                val cartItem = CartItem(
                    name = product.name,
                    price = product.discountPrice ?: product.price,
                    imageUrl = product.imageUrl,
                    count = 1
                )
                sharedViewModel.addToCart(cartItem)

                Toast.makeText(
                    holder.itemView.context,
                    "${product.name} sepete eklendi!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "Bu ürün şu an satışta değil.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // --- Uzun basınca silme (sadece admin) ---
        holder.itemView.setOnLongClickListener {
            if (!isAdmin) return@setOnLongClickListener true

            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Ürünü Sil")
                .setMessage("${product.name} silinsin mi?")
                .setPositiveButton("Evet") { _, _ ->
                    onDeleteProduct?.invoke(product)
                }
                .setNegativeButton("Hayır", null)
                .show()
            true
        }
    }

    // ⭐ DÜZELTME 2 & 3: updateProductStatusInDatabase fonksiyonunu db nesnesini kullanarak düzgün tanımladık.
    private fun updateProductStatusInDatabase(productId: String, isActive: Boolean) {
        db.collection("products") // db nesnesini kullanıyoruz
            .document(productId)
            .update("isActive", isActive)
            .addOnSuccessListener {
                Log.d("ADMIN", "Durum başarıyla güncellendi: $isActive")
            }
            .addOnFailureListener { e ->
                Log.e("ADMIN", "Güncelleme hatası", e)
                // Hata durumunda admini bilgilendir
            }
    }

    override fun getItemCount(): Int = productList.size
}