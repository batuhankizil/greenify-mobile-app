package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.greenify.databinding.ActivityLoginScreenBinding
import com.example.greenify.databinding.ActivityRegisterScreenBinding
import com.google.firebase.auth.FirebaseAuth

class LoginScreen : AppCompatActivity() {

    private lateinit var onBackPressedCallback: OnBackPressedCallback


    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()

        binding.login.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Greenify'a Hoşgeldin", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this, "Hesap bilgileriniz yanlış", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Lütfen email ve şifrenizi giriniz", Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Geri tuşuna basıldığında oturumu kapat ve uygulamadan çık
                FirebaseAuth.getInstance().signOut()
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        binding.register.setOnClickListener {

            val intent = Intent(this, RegisterScreen::class.java)
            startActivity(intent)

        }

        binding.forgotPassword.setOnClickListener {

            val intent = Intent(this, ForgotPassword::class.java)
            startActivity(intent)
        }
    }
}