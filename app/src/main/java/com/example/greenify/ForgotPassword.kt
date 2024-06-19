package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var login: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.email)
        submitButton = findViewById(R.id.submit)
        login = findViewById(R.id.login)

        submitButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()


            if (email.isEmpty()) {
                emailEditText.error = "Lütfen email adresinizi giriniz"
                emailEditText.requestFocus()
                return@setOnClickListener
            }

            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        startActivity(Intent(this, LoginScreen::class.java))
                        finish()
                        Toast.makeText(this, "Şifre sıfırlama e-postası gönderildi", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Geçerli bir mail giriniz", Toast.LENGTH_LONG).show()
                    }
                }
        }

        login.setOnClickListener {
            startActivity(Intent(this, LoginScreen::class.java))
            finish()
        }

    }

}
