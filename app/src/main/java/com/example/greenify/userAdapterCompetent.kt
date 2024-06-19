package com.example.greenify

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore


class UserAdapterCompetent(private val context: Context, private val userList: List<UserCompetent>) : RecyclerView.Adapter<UserAdapterCompetent.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_user_competent, parent, false)

        return UserViewHolder(view)
    }

    fun updateUserList() {
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)

        holder.itemView.setOnClickListener {
            val userId = user.userId

            // Firebase Firestore instance
            val db = FirebaseFirestore.getInstance()

            // Kullanıcının belgesini al
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { userDocument ->
                    // Kullanıcının belgesinden ad, soyad ve telefon numarası bilgilerini al
                    val username = userDocument.getString("userName")
                    val surname = userDocument.getString("userSurname")
                    val phonenumber = userDocument.getLong("phoneNumber").toString()

                    // Waste tablosundan kullanıcının atık ağırlığını al
                    db.collection("waste")
                        .whereEqualTo("user_id", userId)
                        .get()
                        .addOnSuccessListener { wasteQuerySnapshot ->
                            for (document in wasteQuerySnapshot.documents) {
                                val weight = document.getString("weight")
                                val location = document.getString("location")
                                val userSelectedMaterial = document.getString("selected_material")
                                val floor = document.getString("floor")
                                val apartment = document.getString("apartment")
                                val note = document.getString("note")
                                if (weight != null) {
                                    // Weight alanı null değilse, uygun bir işlem yapılabilir
                                    Log.d("Firebase", "User weight: $weight")

                                    // İstenilen işlemi yapabilirsiniz, örneğin, intent'e ekleyebilirsiniz
                                    val intent = Intent(context, WasteDetails::class.java)
                                    intent.putExtra("userId", userId)
                                    intent.putExtra("userName", username)
                                    intent.putExtra("userSurname", surname)
                                    intent.putExtra("userWeight", weight)
                                    intent.putExtra("userLocation", location)
                                    intent.putExtra("phoneNumber", phonenumber)
                                    intent.putExtra("userSelectedMaterial", userSelectedMaterial)
                                    intent.putExtra("apartmentNumber", apartment)
                                    intent.putExtra("floorNumber", floor)
                                    intent.putExtra("userNote", note)
                                    context.startActivity(intent)
                                } else {
                                    // Weight alanı null ise, uygun bir işlem yapılabilir
                                    Log.e("Firebase", "User weight is null")
                                }

                            }
                        }
                        .addOnFailureListener { exception ->
                            // Waste belgesini alma hatası
                            Log.e("Firebase", "Error getting waste document: ", exception)
                        }
                }
                .addOnFailureListener { exception ->
                    // Belgeyi alma hatası
                    Log.e("Firebase", "Error getting user document: ", exception)
                }


        }

    }



    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.userName)
        private val locationTextView: TextView = itemView.findViewById(R.id.userLocation)

        fun bind(UserCompetent: UserCompetent) {
            nameTextView.text = UserCompetent.userName
            locationTextView.text = UserCompetent.location
        }
    }
}
