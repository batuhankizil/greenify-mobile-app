package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class StoreActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    private lateinit var migros100Button: Button
    private lateinit var migros200Button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        migros100Button = findViewById<Button>(R.id.migrosButton)
        migros200Button = findViewById<Button>(R.id.a101Button)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        if (currentUser != null) {
            getUserScore(currentUser.uid)
        } else {
            // Kullanıcı oturum açmamışsa, uygun işlemi yap
        }

        migros100Button.setOnClickListener {
            val decreaseAmount = 100 // Migros ödülünün puan değeri
            currentUser?.let { it1 -> decreaseUserScore(it1.uid, decreaseAmount) }
            addProductToUser(currentUser?.uid ?: "", "Migros 100")
        }

        migros200Button.setOnClickListener {
            val decreaseAmount = 200
            currentUser?.let { it1 -> decreaseUserScore(it1.uid, decreaseAmount) }
            addProductToUser(currentUser?.uid ?: "", "Migros 200")
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUserScore(userId: String) {
        db.collection("users").document(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    val score = document.getLong("score")?.toInt() ?: 0
                    updateButtonStates(score)
                    val scoreTextView: TextView = findViewById(R.id.score)
                    scoreTextView.text = "$score Puan"
                } else {
                    // Belge bulunamadı, hata durumunu ele al
                    updateButtonStates(0)
                }
            } else {
                // Görev başarısız oldu, hata durumunu ele al
                updateButtonStates(0)
            }
        }
    }

    private fun decreaseUserScore(userId: String, decreaseAmount: Int) {
        val amount: Long = decreaseAmount.toLong() // Int değeri Long'a dönüştürüldü
        db.collection("users").document(userId)
            .update("score", FieldValue.increment(amount * -1))
            .addOnSuccessListener {
                // Puan başarıyla düşürüldüğünde yapılacak işlemler
                Toast.makeText(this, "Çekiniz kullanılmaya hazır", Toast.LENGTH_SHORT).show()
                getUserScore(userId)
            }
            .addOnFailureListener { e ->
                // Puan düşürülürken bir hata oluştuğunda yapılacak işlemler
            }
    }

    private fun updateButtonStates(score: Int) {
        updateButtonState(migros100Button, score, 100)
        updateButtonState(migros200Button, score, 200)
    }

    private fun updateButtonState(button: Button, score: Int, requiredScore: Int) {
        if (score >= requiredScore) {
            button.isEnabled = true
            button.alpha = 1.0f // Tam görünürlük
        } else {
            button.isEnabled = false
            button.alpha = 0.5f // Silik görünürlük
        }
    }

    // Kullanıcının aldığı ürünleri Firestore'a ekleyen fonksiyon
    private fun addProductToUser(userId: String, productName: String) {
        val randomCode = generateRandomCode()
        val userProduct = hashMapOf(
            "productName" to productName,
            "code" to randomCode
        )

        db.collection("user_products")
            .document(userId)
            .collection("products")
            .add(userProduct)
            .addOnSuccessListener { documentReference ->
                // Ürün başarıyla eklendi, rastgele kodu alıp RewardActivity'e gönder
                val code = randomCode.toString()
                val intent = Intent(this, Reward::class.java).apply {
                    putExtra("products", arrayListOf(productName))
                    putExtra("code", code)
                }
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                // Hata durumunda işlemler
            }
    }


    // Rastgele harfli 10 haneli bir sayısal kod üreten fonksiyon
    private fun generateRandomCode(): String {
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val random = Random()
        val code = StringBuilder()
        repeat(10) {
            val index = random.nextInt(alphabet.length)
            code.append(alphabet[index])
        }
        return code.toString()
    }
}
