package com.example.greenify

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Firebase Authentication ile oturum açık olan kullanıcının bilgilerine erişin
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Firebase Firestore'dan kullanıcı adını alın
        currentUser?.let { user ->
            val db = FirebaseFirestore.getInstance()
            val userId = user.uid

            // ImageView'i bulma
            val imageView = view.findViewById<ImageView>(R.id.profileImageView)

            // Firebase Storage'dan varsayılan profil resmini alıp ImageView'de gösterme
            val storageRef = FirebaseStorage.getInstance().reference
            val defaultImageRef = storageRef.child("profil_images/$userId/userprofile.png")

            defaultImageRef.downloadUrl.addOnSuccessListener { uri ->
                // Resim başarıyla indirildiğinde
                Glide.with(this)
                    .load(uri)
                    .override(0,0)
                    .into(imageView)
            }.addOnFailureListener {
                // Resim indirilemediğinde
                Toast.makeText(requireContext(), "Varsayılan profil resmi yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
            }

            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("userName")
                        val userScore = document.getLong("score")
                        val wasteScore = document.getLong("waste")
                        val aeroplaneScore = document.getLong("aeroplane")
                        val carScore = document.getLong("car")
                        val treeScore = document.getLong("tree")

                        view.findViewById<TextView>(R.id.userFullName).text = "$userName"
                        view.findViewById<TextView>(R.id.score).text = "$userScore Puan"
                        view.findViewById<TextView>(R.id.waste).text = "$wasteScore kg atık geri dönüştürdün"
                        view.findViewById<TextView>(R.id.aeroplane).text = "1 kişinin $aeroplaneScore dakikalık uçak yolculuğu"
                        view.findViewById<TextView>(R.id.car).text = "Ortalama bir binek aracın $carScore km yol yapması"
                        view.findViewById<TextView>(R.id.tree).text = "$treeScore yetişkin ağacın yıllık CO2 emme kapasitesi"

                    } else {
                        println("Belirtilen kullanıcı bulunamadı.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Kullanıcı adı alınırken hata oluştu: $exception")
                }
        }
    }
}