package com.example.greenify

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RecyclePreview : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle_preview)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // onCreate içinde
        val selectedMaterial = intent.getStringExtra("selected_material")
        val weight = intent.getStringExtra("weight")
        val location = intent.getStringExtra("location")
        val district = intent.getStringExtra("district")
        val apartment = intent.getStringExtra("apartmentNumber")
        val floor = intent.getStringExtra("floorNumber")
        val note = intent.getStringExtra("note")
        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)

        val materialTextView: TextView = findViewById(R.id.materialTextView)
        val weightTextView: TextView = findViewById(R.id.editTextWeight)
        val currentLocation: TextView = findViewById(R.id.currentLocation)
        val apartmentNumber: TextView = findViewById(R.id.apartmentNumber)
        val floorNumber: TextView = findViewById(R.id.floorNumber)
        val noteUser: TextView = findViewById(R.id.note)
        val latitudeMap: TextView = findViewById(R.id.latitude)
        val longitudeMap: TextView = findViewById(R.id.longitude)

        materialTextView.text = "$selectedMaterial"
        weightTextView.text = "$weight KG"
        currentLocation.text = "$location"
        apartmentNumber.text = "$apartment"
        floorNumber.text = "$floor"
        noteUser.text = "$note"
        latitudeMap.text = "$latitude"
        longitudeMap.text = "$longitude"

        val completeButton: Button = findViewById(R.id.completeButton)

        // completeButton'a tıklama dinleyicisi ekle
        completeButton.setOnClickListener {

            // Firestore veritabanı referansını al
            val db = FirebaseFirestore.getInstance()

            // Firestore'a veri ekleme işlemi
            val data = hashMapOf(
                "selected_material" to selectedMaterial,
                "weight" to weight,
                "location" to location,
                "district" to district,
                "apartment" to apartment,
                "floor" to floor,
                "note" to note,
                "latitude" to latitude,
                "longitude" to longitude,
                "user_id" to FirebaseAuth.getInstance().currentUser?.uid // Kullanıcının UID'si
            )
            db.collection("waste")
                .add(data)
                .addOnSuccessListener { documentReference ->
                    // Başarılı ekleme durumunda kullanıcıya geri bildirim ver
                    Toast.makeText(this, "Atığınız başarıyla bildirildi", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Hata durumunda kullanıcıya geri bildirim ver
                    Toast.makeText(this, "Veri kaydedilirken bir hata oluştu.", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error adding document", e)
                }
            showCustomDialog()
        }

    }

    fun showCustomDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)

        // AlertDialog.Builder kullanarak özel bir dialog oluştur
        val builder = AlertDialog.Builder(this, R.style.MyCustomAlertDialog)
        builder.setView(dialogView)

        // AlertDialog nesnesini oluştur
        val alertDialog = builder.create()

        // AlertDialog'ı göstermeden önce hizalamayı ayarla
        val layoutParams = WindowManager.LayoutParams().apply {
            copyFrom(alertDialog.window?.attributes)
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.CENTER // Ekranın hem dikey hem de yatay ortasına hizala
        }
        alertDialog.window?.attributes = layoutParams

        val tamamButton = dialogView.findViewById<Button>(R.id.dialogButton)
        tamamButton.setOnClickListener {
            alertDialog.dismiss() // Dialogu kapat
            // MainActivity'e geri dön
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        // AlertDialog'ı göster
        alertDialog.show()
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}