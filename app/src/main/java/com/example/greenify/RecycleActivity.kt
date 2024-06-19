package com.example.greenify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty

class RecycleActivity : AppCompatActivity() {

    private var counter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycle)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }



        val spinner: Spinner = findViewById(R.id.spinner)
        val items = listOf("Seçiniz", "Yağ", "Elektronik", "Cam", "Kağıt", "Pil", "Kıyafet", "Plastik")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        val editText: EditText = findViewById(R.id.editText)
        val plusButton: Button = findViewById(R.id.plusButton)
        val minusButton: Button = findViewById(R.id.minusButton)

        editText.setText(counter.toString())




        plusButton.setOnClickListener {
            val currentValue = editText.text.toString().toIntOrNull() ?: 0
            val newValue = currentValue + 1
            editText.setText(newValue.toString())
        }

        minusButton.setOnClickListener {
            val currentValue = editText.text.toString().toIntOrNull() ?: 0
            if (currentValue > 0) {
                val newValue = currentValue - 1
                editText.setText(newValue.toString())
            }
        }


        val continueButton: Button = findViewById(R.id.continueButton)

        continueButton.setOnClickListener {

            val weightEditText = editText.text.toString()
            val selectedMaterial = spinner.selectedItem.toString()

            if (selectedMaterial.isEmpty() || selectedMaterial == "Seçiniz") {
                Toast.makeText(this, "Lütfen atığınızı seçin", Toast.LENGTH_SHORT).show()
            }

            else if (weightEditText == "0") {
                Toast.makeText(this, "Lütfen atığınızın ağırlığını girin (yaklaşık)", Toast.LENGTH_SHORT).show()
            }

            else {
                val selectedMaterial = spinner.selectedItem.toString() // Seçilen malzeme
                val weight = editText.text.toString() // Girilen ağırlık
                val intent = Intent(this, RecycleContinueActivity::class.java).apply {
                    putExtra("selected_material", selectedMaterial)
                    putExtra("weight", weight)
                }
                startActivity(intent)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}