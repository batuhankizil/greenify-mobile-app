package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class CompetentLoginScreen : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_competent_login_screen)

        val editTextUsername = findViewById<EditText>(R.id.username)
        val editTextPassword = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.login)

        buttonLogin.setOnClickListener {
            val username = editTextUsername.text.toString()
            val password = editTextPassword.text.toString()

            // Kullanıcı adı ve şifreyi Firestore'dan kontrol et
            db.collection("competent")
                .whereEqualTo("username", username)
                .whereEqualTo("password", password)
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // Kullanıcı doğrulama başarılı
                        val competentDocument = documents.documents[0]
                        val district = competentDocument.getString("district") // İlçe bilgisini Firestore'dan al
                        val userId = competentDocument.id
                        val competentName = competentDocument.getString("username")
                        val competentLatitude = competentDocument.getDouble("latitude")
                        val competentLongitude = competentDocument.getDouble("longitude")

                        Toast.makeText(this, "Giriş başarılı", Toast.LENGTH_SHORT).show()

                        // İlçe bilgisini CompetentScreen'e ve diğer bilgileri geçir
                        val intent = Intent(this, CompetentScreen::class.java)
                        intent.putExtra("district", district)
                        intent.putExtra("user_id", userId)
                        intent.putExtra("username", competentName)
                        intent.putExtra("latitude", competentLatitude)
                        intent.putExtra("longitude", competentLongitude)


                        startActivity(intent)

                    } else {
                        // Kullanıcı doğrulama başarısız
                        Toast.makeText(this, "Kullanıcı adınız veya şifreniz yanlış. Yetkili ile iletişime geçiniz.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Firestore sorgusu başarısız oldu
                    Toast.makeText(this, "Sorgu başarısız: $exception", Toast.LENGTH_SHORT).show()
                }
        }
    }
}