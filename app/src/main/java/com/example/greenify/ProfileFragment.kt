package com.example.greenify

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        val buttonLogout: Button = view.findViewById(R.id.logout)
        val userPorfileInfo: Button = view.findViewById(R.id.userPorfileInfo)
        val store: Button = view.findViewById(R.id.store)
        val reward: Button = view.findViewById(R.id.reward)


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

                        view.findViewById<TextView>(R.id.userFullName).text = "Hoşgeldin, $userName"

                    } else {
                        println("Belirtilen kullanıcı bulunamadı.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Kullanıcı adı alınırken hata oluştu: $exception")
                }
        }



        buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val  intent = Intent(activity, LoginScreen::class.java)
            startActivity(intent)
        }

        userPorfileInfo.setOnClickListener {
            val  intent = Intent(activity, UserProfileInfo::class.java)
            startActivity(intent)
        }

        store.setOnClickListener {
            val  intent = Intent(activity, StoreActivity::class.java)
            startActivity(intent)
        }

        reward.setOnClickListener {
            val  intent = Intent(activity, Reward::class.java)
            startActivity(intent)
        }




        return view
    }

}