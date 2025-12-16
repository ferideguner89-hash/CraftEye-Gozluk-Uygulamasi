package com.example.crafteye8
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    var userName: String? = null

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> get() = _cartItems

    // ✅ Seçilen ürün (admin update)
    val selectedProduct = MutableLiveData<Product?>()

    // -------------------- SEPET --------------------

    fun addToCart(item: CartItem) {
        val currentList = _cartItems.value ?: mutableListOf()

        val existingItem = currentList.find { it.imageUrl == item.imageUrl }

        if (existingItem != null) {
            existingItem.count += item.count
        } else {
            currentList.add(item)
        }

        _cartItems.value = currentList.toMutableList()
    }

    fun increaseQuantity(item: CartItem) {
        val currentList = _cartItems.value ?: return
        val existingItem = currentList.find { it.imageUrl == item.imageUrl }

        existingItem?.let {
            it.count++
            _cartItems.value = currentList.toMutableList()
        }
    }

    fun decreaseQuantity(item: CartItem) {
        val currentList = _cartItems.value ?: return
        val existingItem = currentList.find { it.imageUrl == item.imageUrl }

        existingItem?.let {
            if (it.count > 1) {
                it.count--
            } else {
                currentList.remove(it)
            }
            _cartItems.value = currentList.toMutableList()
        }
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }

    // -------------------- FAVORİ --------------------

    val favoriteList = ArrayList<Product>()

    fun addFavorite(product: Product) {
        if (!favoriteList.any { it.name == product.name }) {
            favoriteList.add(product)
            product.isFavorite = true
        }
    }

    fun removeFavorite(product: Product) {
        favoriteList.removeAll { it.name == product.name }
        product.isFavorite = false
    }

    fun isFavorite(product: Product): Boolean {
        return favoriteList.any { it.name == product.name }
    }
}