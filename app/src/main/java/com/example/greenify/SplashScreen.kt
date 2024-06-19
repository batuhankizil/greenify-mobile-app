package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserSession()
        }, 3000)
    }

    private fun checkUserSession() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val intent: Intent = if (currentUser != null) {
            // Oturum açık ise ana ekrana yönlendir
            Intent(this, MainActivity::class.java)
        } else {
            // Oturum kapalı ise giriş ekranına yönlendir
            Intent(this, LoginRegisterScreen::class.java)
        }
        startActivity(intent)
        finish() // Aktiviteyi sonlandır

    }
}