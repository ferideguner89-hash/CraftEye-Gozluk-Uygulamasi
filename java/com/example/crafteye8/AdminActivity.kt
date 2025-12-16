package com.example.crafteye8

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.crafteye8.databinding.ActivityAdminBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // 1) Order verisini al
        val orderData = intent.getParcelableExtra<Order>("orderData")

        // 2) NavController'ı al
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_admin) as NavHostFragment
        val navController = navHostFragment.navController

        // 3) Eğer sipariş varsa direkt Ad3Fragment'a yönlendir
        if (orderData != null) {
            val bundle = Bundle()
            bundle.putParcelable("orderData", orderData)

            navController.setGraph(R.navigation.nav_graph, bundle)
        }

        // 4) Bottom Nav kurulumu
        val bottomNav = findViewById<BottomNavigationView>(R.id.admin_bottom_nav)
        bottomNav.setupWithNavController(navController)
    }
}