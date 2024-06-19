package com.example.greenify

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.TextView
import android.widget.Toast
import com.example.greenify.databinding.ActivityRegisterScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class RegisterScreen : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.register.setOnClickListener {
            val userName = binding.userName.text.toString()
            val userSurname = binding.userSurname.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val rePassword = binding.rePassword.text.toString()
            val phoneNumber = binding.phoneNumber.text.toString()

            if (userName.isNotEmpty() && userSurname.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && rePassword.isNotEmpty() && phoneNumber.isNotEmpty()) {
                if (password == rePassword) {
                    val phoneNumber = phoneNumber.toInt()
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                val userId = user?.uid
                                if (userId != null) {


                                    val db = FirebaseFirestore.getInstance()
                                    val userData = hashMapOf(
                                        "userName" to userName,
                                        "userSurname" to userSurname,
                                        "email" to email,
                                        "score" to 0,
                                        "phoneNumber" to phoneNumber,
                                        "waste" to 0,
                                        "aeroplane" to 0,
                                        "car" to 0,
                                        "tree" to 0
                                    )

                                    db.collection("users").document(userId)
                                        .set(userData)
                                        .addOnSuccessListener {

                                            // Kullanıcı başarıyla kaydedildiğinde
                                            val defaultImageRef = FirebaseStorage.getInstance().reference.child("profil_images/$userId/userprofile.png")
                                            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.user_profile)
                                            val baos = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                                            val data = baos.toByteArray()

                                            defaultImageRef.putBytes(data)
                                                .addOnSuccessListener {
                                                    // Varsayılan profil resmi başarıyla yüklendi
                                                    //Toast.makeText(this, "Varsayılan profil resmi başarıyla yüklendi", Toast.LENGTH_SHORT).show()
                                                }
                                                .addOnFailureListener { e ->
                                                    // Varsayılan profil resmi yüklenirken hata oluştu
                                                    Toast.makeText(this, "Profil resmi yüklenirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                                                }

                                            // Kullanıcı başarıyla kaydedildiğinde
                                            val defaultImageUri = Uri.parse("android.resource://com.example.greenify/drawable/userProfile")
                                            val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                                                .setPhotoUri(defaultImageUri)
                                                .build()
                                            user.updateProfile(userProfileChangeRequest)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        Toast.makeText(this, "Başarıyla kayıt oldunuz.", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this, MainActivity::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    } else {
                                                        Toast.makeText(this, "Kullanıcı profili güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Firestore'a veri kaydedilirken bir hata oluştu: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(this, "Kullanıcı kimliği alınamadı", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Hata: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Şifreler eşleşmiyor", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Lütfen tüm bilgileri giriniz", Toast.LENGTH_SHORT).show()
            }

        }

        binding.login.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

    }

}