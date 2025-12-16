package com.example.crafteye8

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.text.Editable
import android.text.TextWatcher
import android.widget.*

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class Ad2Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var edtSearch: EditText
    private lateinit var edtUserName: EditText
    private lateinit var edtUserEmail: EditText
    private lateinit var switchAccountActive: Switch
    private lateinit var btnUpdateUser: Button
    private lateinit var btnDeleteUser: Button

    // Firebase
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userList = ArrayList<User>()
    private var selectedUser: User? = null
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_ad2, container, false)

        // View bağlama
        recyclerView = view.findViewById(R.id.recyclerUsers)
        edtSearch = view.findViewById(R.id.edtSearchUser)
        edtUserName = view.findViewById(R.id.edtUserName)
        edtUserEmail = view.findViewById(R.id.edtUserEmail)
        switchAccountActive = view.findViewById(R.id.switchAccountActive)
        btnUpdateUser = view.findViewById(R.id.btnUpdateUser)
        btnDeleteUser = view.findViewById(R.id.btnDeleteUser)

        // RecyclerView ayarları
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = UserAdapter(userList) { user ->
            onUserSelected(user)}
            recyclerView.adapter = adapter

        // Kullanıcıları yükle
        loadUsers()

        // Arama çubuğu
        setupSearch()

        // Butonlar
        setupButtons()

        return view
    }

    // Firestore’dan kullanıcıları çek
    private fun loadUsers() {
        db.collection("users").get()
            .addOnSuccessListener { documents ->
                userList.clear()
                for (doc in documents) {
                    val user = doc.toObject(User::class.java)?.apply { id = doc.id }
                    if (user != null && user.id != auth.currentUser?.uid) { // Admini göstermiyoruz
                        userList.add(user)
                    }
                }
                adapter.updateList(userList)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Kullanıcılar yüklenemedi", Toast.LENGTH_SHORT).show()
            }
    }

    // Arama fonksiyonu
    private fun setupSearch() {
        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val filteredList = userList.filter {
                    it.name.contains(s.toString(), ignoreCase = true) ||
                            it.email.contains(s.toString(), ignoreCase = true)
                }
                adapter.updateList(filteredList)
            }
        })
    }

    // Kullanıcı seçildiğinde bilgileri göster
    private fun onUserSelected(user: User) {
        selectedUser = user
        edtUserName.setText(user.name)
        edtUserEmail.setText(user.email)
        switchAccountActive.isChecked = user.isActive
    }

    // Güncelle ve Sil butonları
    private fun setupButtons() {
        // Güncelleme
        btnUpdateUser.setOnClickListener {
            val user = selectedUser ?: return@setOnClickListener
            val updates = hashMapOf(
                "name" to edtUserName.text.toString().trim(),
                "email" to edtUserEmail.text.toString().trim(),
                "isActive" to switchAccountActive.isChecked
            )

            db.collection("users").document(user.id)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Kullanıcı güncellendi", Toast.LENGTH_SHORT).show()
                    loadUsers()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Güncelleme başarısız", Toast.LENGTH_SHORT).show()
                }
        }

        // Silme
        btnDeleteUser.setOnClickListener {
            val user = selectedUser
            if (user == null) {
                Toast.makeText(context, "Lütfen bir kullanıcı seçin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                db.collection("users").document(user.id)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Kullanıcı silindi", Toast.LENGTH_SHORT).show()

                        // userList'ten kaldır ve adapteri güncelle
                        userList.removeAll { it.id == user.id }
                        adapter.updateList(userList)

                        // Seçimi temizle
                        selectedUser = null
                        edtUserName.setText("")
                        edtUserEmail.setText("")
                        switchAccountActive.isChecked = false
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Silme başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } catch (e: Exception) {
                Toast.makeText(context, "Silme hatası: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


// RecyclerView Adapter
