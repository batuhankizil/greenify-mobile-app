package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LoginRegisterScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register_screen)

        val login: TextView = findViewById(R.id.login)
        val register: TextView = findViewById(R.id.register)
        val competentLogin: TextView = findViewById(R.id.competentLogin)

        login.setOnClickListener {

            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)

        }

        register.setOnClickListener {

            val intent = Intent(this, RegisterScreen::class.java)
            startActivity(intent)

        }

        competentLogin.setOnClickListener {

            val intent = Intent(this, CompetentLoginScreen::class.java)
            startActivity(intent)

        }
    }
}