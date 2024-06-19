package com.example.greenify

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Reward : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reward)

        val productsLayout = findViewById<LinearLayout>(R.id.productsLayout)

        // Firestore'dan ürünleri al
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("user_products").document(userId).collection("products")
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        val noRewardTextView = TextView(this)
                        noRewardTextView.text = "Ödülünüz bulunmamaktadır."
                        productsLayout.addView(noRewardTextView)
                    } else {
                        for (document in documents) {
                            val productName = document.getString("productName")
                            val code = document.getString("code")
                            if (productName != null && code != null) {
                                addProductToLayout(productsLayout, productName, code)
                            }
                        }
                    }

                }
                .addOnFailureListener { exception ->
                    // Hata durumunda işlemler
                }
        }
    }

    private fun addProductToLayout(layout: LinearLayout, productName: String, code: String) {
        val productTextView = TextView(this)
        productTextView.text = "$productName ₺ - Kod: $code"
        layout.addView(productTextView)
    }

}