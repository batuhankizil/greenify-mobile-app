package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class CompetentScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapterCompetent
    private lateinit var userList: MutableList<UserCompetent>
    private lateinit var competentNameTextView: TextView




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competent_screen)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userList = mutableListOf()
        adapter = UserAdapterCompetent(this, userList)
        recyclerView.adapter = adapter

        competentNameTextView = findViewById(R.id.competentName)


        val competentDistrict = intent.getStringExtra("district")
        val username = intent.getStringExtra("username")

        competentNameTextView.text = username.toString()




        // Firebase Firestore instance
        val db = FirebaseFirestore.getInstance()



        // Kullanıcılar koleksiyonundan veri al
        db.collection("users")
            .get()
            .addOnSuccessListener { userResult ->
                val userMap = mutableMapOf<String, String>()

                // Kullanıcı bilgilerini al ve userMap'e ekle
                for (userDocument in userResult) {
                    val userId = userDocument.id
                    val userName = userDocument.getString("userName")
                    if (userName != null) {
                        userMap[userId] = userName
                    }
                }

                // Atıklar koleksiyonundan veri al
                db.collection("waste")
                    .whereEqualTo("district", competentDistrict)
                    .get()
                    .addOnSuccessListener { wasteResult ->
                        // Atıkların bilgilerini al ve RecyclerView'e ekle
                        for (wasteDocument in wasteResult) {
                            val userId = wasteDocument.getString("user_id")
                            val location = wasteDocument.getString("location")
                            val userDistrict = wasteDocument.getString("district")
                            val userName = userMap[userId]
                            if (userName != null && location != null && userId != null) {
                                userList.add(UserCompetent(userName, location, userId, null))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                    .addOnFailureListener { exception ->
                        // Atık verilerini alma hatası
                        Log.e("Firebase", "Error getting waste documents: ", exception)
                    }
            }            .addOnFailureListener { exception ->
                // Kullanıcı verilerini alma hatası
                Log.e("Firebase", "Error getting user documents: ", exception)
            }

    }
}