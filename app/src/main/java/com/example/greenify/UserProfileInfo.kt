package com.example.greenify

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class UserProfileInfo : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val IMAGE_PICK_REQUEST = 1


    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile_info)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // Firebase Authentication ile oturum açık olan kullanıcının bilgilerine erişin
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Firebase Firestore'dan kullanıcı adını alın
        currentUser?.let { user ->
            val userId = user.uid

            // ImageView'i bulma
            imageView = findViewById<ImageView>(R.id.profileImageView)

            // Firebase Storage'dan varsayılan profil resmini alıp ImageView'de gösterme
            val storageRef = FirebaseStorage.getInstance().reference
            val defaultImageRef = storageRef.child("profil_images/$userId/userprofile.png")

            defaultImageRef.downloadUrl.addOnSuccessListener { uri ->
                // Resim başarıyla indirildiğinde
                Glide.with(this)
                    .load(uri)
                    .override(0, 0)
                    .into(imageView)
            }.addOnFailureListener {
                // Resim indirilemediğinde
                Toast.makeText(this, "Varsayılan profil resmi yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
            }

            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && !isDestroyed) {
                        val userName = document.getString("userName")
                        val userSurname = document.getString("userSurname")
                        val email = document.getString("email")
                        val phoneNumber = document.getLong("phoneNumber")

                        findViewById<TextView>(R.id.email).text = "$email"
                        findViewById<TextView>(R.id.userFullName).text = "$userName $userSurname"
                        findViewById<TextView>(R.id.userName).text = "$userName"
                        findViewById<TextView>(R.id.userSurname).text = "$userSurname"
                        findViewById<TextView>(R.id.phoneNumber).text = "$phoneNumber"
                    } else {
                        println("Belirtilen kullanıcı bulunamadı.")
                    }
                }
                .addOnFailureListener { exception ->
                    if (!isDestroyed) {
                        println("Kullanıcı adı alınırken hata oluştu: $exception")
                    }
                }
        }

        // Resmi değiştirme butonu
        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_REQUEST)
        }

        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveProfileChanges()
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                val userId = user.uid
                val email = findViewById<TextView>(R.id.email).text.toString()
                val name = findViewById<TextView>(R.id.userName).text.toString()
                val surname = findViewById<TextView>(R.id.userSurname).text.toString()
                val phoneNumber = findViewById<TextView>(R.id.phoneNumber).text.toString()

                updateUserProfile(email, name, surname, phoneNumber, userId)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveProfileChanges() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        currentUserUid?.let { uid ->
            val profileImageRef = FirebaseStorage.getInstance().reference
                .child("profil_images")
                .child(uid)
                .child("userprofile.png") // ya da istediğiniz bir dosya adı

            // Kullanıcının galeriden seçtiği resmi al
            val imageUri = selectedImageUri

            if (imageUri != null) {
                profileImageRef.putFile(imageUri)
                    .addOnSuccessListener { _ ->
                        // Yükleme başarılıysa
                        profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Yükleme tamamlandıktan sonra resmin URL'sini alın
                            val imageUrl = uri.toString()

                            // Firestore'da kullanıcının profil resmini güncelle
                            val db = FirebaseFirestore.getInstance()
                            db.collection("users").document(uid)
                                .update("profileImage", imageUrl)
                                .addOnSuccessListener {
                                    // Firestore güncelleme başarılıysa
                                    Toast.makeText(this, "Profil resmi güncellendi", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { exception ->
                                    // Firestore güncelleme başarısızsa
                                    Toast.makeText(this, "Profil resmi güncellenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Yükleme başarısızsa
                        Toast.makeText(this, "Resim yüklenirken bir hata oluştu", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Kullanıcı resim seçmemişse veya bir hata oluşmuşsa
            }
        }
    }

    fun updateUserProfile(email: String, userName: String, userSurname: String, phoneNumber: String, userId: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()

        // Kullanıcı bilgilerini güncelle
        val user = hashMapOf(
            "email" to email,
            "userName" to userName,
            "userSurname" to userSurname,
            "phoneNumber" to phoneNumber
        )

        db.collection("users").document(userId)
            .update(user as Map<String, Any>)
            .addOnSuccessListener {
                // Başarıyla güncellendi
                Toast.makeText(this, "Bilgileriniz güncellendi", Toast.LENGTH_SHORT).show()
                println("Kullanıcı bilgileri güncellendi")
            }
            .addOnFailureListener { e ->
                // Hata oluştu
                println("Hata: $e")
            }

        // Firebase Authentication ile de kullanıcı email bilgisini güncelle
        auth.currentUser?.updateEmail(email)
            ?.addOnSuccessListener {
                // Başarıyla güncellendi
                println("Email güncellendi")
            }
            ?.addOnFailureListener { e ->
                // Hata oluştu
                println("Hata: $e")
            }
    }


    // Kullanıcı resmini seçtiğinde bu URI'yi güncellemek için bir değişken tanımlayın
    private var selectedImageUri: Uri? = null

    // Resmi değiştirme butonuna tıklama işlemlerini onActivityResult içine taşıyın
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            // Kullanıcının seçtiği resmin URI'sini al
            selectedImageUri = data.data

            // ImageView'da göster
            imageView.setImageURI(selectedImageUri)
        }
    }

}


