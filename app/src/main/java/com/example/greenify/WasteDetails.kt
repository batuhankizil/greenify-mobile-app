package com.example.greenify

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class WasteDetails : AppCompatActivity() {

    private var adapter: UserAdapterCompetent? = null


    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waste_details)

        val completeButton = findViewById<Button>(R.id.completedButton)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val userName = intent.getStringExtra("userName")
        val userSurname = intent.getStringExtra("userSurname")
        val userPhoneNumber = intent.getStringExtra("phoneNumber")
        val userWeight = intent.getStringExtra("userWeight")?.toIntOrNull() ?: 0
        val userLocation = intent.getStringExtra("userLocation")
        val userSelectedMaterial = intent.getStringExtra("userSelectedMaterial")
        val apartmentNumber = intent.getStringExtra("apartmentNumber")
        val floorNumber = intent.getStringExtra("floorNumber")
        val userNote = intent.getStringExtra("userNote")

        val userNameTextView: TextView = findViewById(R.id.userName)
        val userSurnameTextView: TextView = findViewById(R.id.userSurname)
        val userPhoneNumberTextView: TextView = findViewById(R.id.userPhoneNumber)
        val userWeightTextView: TextView = findViewById(R.id.userWeight)
        val userLocationTextView: TextView = findViewById(R.id.userLocation)
        val userSelectedMaterialTextView: TextView = findViewById(R.id.userSelectedMaterial)
        val userApartmentNumberTextView: TextView = findViewById(R.id.apartmentNumber)
        val userFloorNumberTextView: TextView = findViewById(R.id.floorNumber)
        val userNoteTextView: TextView = findViewById(R.id.note)

        userNameTextView.text = "$userName"
        userSurnameTextView.text = "$userSurname"
        userPhoneNumberTextView.text = "$userPhoneNumber"
        userWeightTextView.text = "$userWeight"
        userLocationTextView.text = "$userLocation"
        userSelectedMaterialTextView.text = "$userSelectedMaterial"
        userApartmentNumberTextView.text = "$apartmentNumber"
        userFloorNumberTextView.text = "$floorNumber"
        userNoteTextView.text = "$userNote"


        val scoreToAdd = when (userSelectedMaterial) {
            "Yağ" -> 2 * userWeight
            "Elektronik" -> 10 * userWeight
            "Cam" -> 2 * userWeight
            "Kağıt" -> 2 * userWeight
            "Pil" -> 3 * userWeight
            "Kıyafet" -> 4 * userWeight
            "Plastik" -> 4 * userWeight
            else -> 0 // Diğer malzemeler için varsayılan puan
        }

        val userImpactScore = when (userSelectedMaterial) {
            "Yağ" -> 2 * userWeight
            "Elektronik" -> 2 * userWeight
            "Cam" -> 2 * userWeight
            "Kağıt" -> 2 * userWeight
            "Pil" -> 2 * userWeight
            "Kıyafet" -> 2 * userWeight
            "Plastik" -> 2 * userWeight
            else -> 0
        }

        val impactScore = when (userSelectedMaterial) {
            "Yağ" -> 2 * userWeight
            "Elektronik" -> 2 * userWeight
            "Cam" -> 2 * userWeight
            "Kağıt" -> 2 * userWeight
            "Pil" -> 2 * userWeight
            "Kıyafet" -> 2 * userWeight
            "Plastik" -> 2 * userWeight
            else -> 0
        }

        val wasteScore = when (userSelectedMaterial) {
            "Yağ" -> 1 * userWeight
            "Elektronik" -> 1 * userWeight
            "Cam" -> 1 * userWeight
            "Kağıt" -> 1 * userWeight
            "Pil" -> 1 * userWeight
            "Kıyafet" -> 1 * userWeight
            "Plastik" -> 1 * userWeight
            else -> 0
        }

        val aeroplaneImpact = when (userSelectedMaterial) {
            "Yağ" -> (userWeight * 2.3) / (15 * 0.115)
            "Elektronik" -> 1 * userWeight
            "Cam" -> (userWeight * 2.3) / (15 * 0.115)
            "Kağıt" -> (userWeight * 2.3) / (15 * 0.115)
            "Pil" -> (userWeight * 2.3) / (15 * 0.115)
            "Kıyafet" -> 1 * userWeight
            "Plastik" -> (userWeight * 2.3) / (15 * 0.115)
            else -> 0
        }

        val carImpact = when (userSelectedMaterial) {
            "Yağ" -> (userWeight * 2.3) / 0.2
            "Elektronik" -> 1 * userWeight
            "Cam" -> (userWeight * 2.3) / 0.2
            "Kağıt" -> (userWeight * 2.3) / 0.2
            "Pil" -> (userWeight * 2.3) / 0.2
            "Kıyafet" -> 1 * userWeight
            "Plastik" -> (userWeight * 2.3) / 0.2
            else -> 0
        }

        val treeImpact = when (userSelectedMaterial) {
            "Yağ" -> (userWeight * 2.3) / 22.0
            "Elektronik" -> 1 * userWeight
            "Cam" -> (userWeight * 2.3) / 22.0
            "Kağıt" -> (userWeight * 2.3) / 22.0
            "Pil" -> (userWeight * 2.3) / 22.0
            "Kıyafet" -> 1 * userWeight
            "Plastik" -> (userWeight * 2.3) / 22.0
            else -> 0
        }


        val completedButton = findViewById<Button>(R.id.completedButton)

        completedButton.setOnClickListener {
            val userId: String? = intent.getStringExtra("userId")


            if (userId != null) {
                val db = FirebaseFirestore.getInstance()
                val userRef = db.collection("users").document(userId)

                userRef.update("car", FieldValue.increment(carImpact.toLong()))
                userRef.update("aeroplane", FieldValue.increment(aeroplaneImpact.toLong()))
                userRef.update("tree", FieldValue.increment(treeImpact.toLong()))
                userRef.update("waste", FieldValue.increment(wasteScore.toLong()))


                // impact koleksiyonundaki ilk belgeyi alın
                db.collection("impact").limit(1).get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            val impactDoc = querySnapshot.documents[0].reference
                            impactDoc.update("car", FieldValue.increment(carImpact.toLong()))
                            impactDoc.update("aeroplane", FieldValue.increment(aeroplaneImpact.toLong()))
                            impactDoc.update("tree", FieldValue.increment(treeImpact.toLong()))
                            impactDoc.update("waste", FieldValue.increment(wasteScore.toLong()))
                                .addOnSuccessListener {
                                    Log.d(
                                        "Firebase",
                                        "Impact car score incremented by $impactScore"
                                    )
                                }
                        }
                    }

                // Kullanıcıya puan ekleyin
                userRef.update("score", FieldValue.increment(scoreToAdd.toLong()))
                    .addOnSuccessListener {
                        Log.d("Firebase", "User score incremented by $scoreToAdd")

                        //val impactRef = db.collection("impact").document("carDocumentId") // impact koleksiyonundaki belgenin ID'si
                        //impactRef.update("car", FieldValue.increment(impactScore.toLong()))
                        //impactRef.update("aeroplane", FieldValue.increment(impactScore.toLong()))
                        //impactRef.update("tree", FieldValue.increment(impactScore.toLong()))
                        //impactRef.update("waste", FieldValue.increment(impactScore.toLong()))


                        // Firebase Firestore'dan ilgili atık verilerini silin
                        db.collection("waste")
                            .whereEqualTo("user_id", userId)
                            .get()
                            .addOnSuccessListener { wasteQuerySnapshot ->
                                for (document in wasteQuerySnapshot.documents) {
                                    db.collection("waste").document(document.id).delete()
                                        .addOnSuccessListener {
                                            Log.d("Firebase", "Waste data deleted successfully from Firestore")
                                        }
                                        .addOnFailureListener { exception ->
                                            Log.e("Firebase", "Error deleting waste data from Firestore: ", exception)
                                            Toast.makeText(this, "Failed to delete waste data", Toast.LENGTH_SHORT).show()
                                        }
                                }

                                // RecyclerView'dan ilgili öğeyi kaldır
                                val intent = Intent()
                                intent.putExtra("deletedUserId", userId)
                                setResult(Activity.RESULT_OK, intent)
                                finish() // Detay sayfasını kapat
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firebase", "Error fetching waste data from Firestore: ", exception)
                            }

                        // Kullanıcıya geribildirimde bulunabilirsiniz
                        // Sonucu ana aktiviteye döndür                        val intent = Intent()
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firebase", "Error incrementing user score: ", exception)
                        // Hata durumunda kullanıcıya bir bildirim yapılabilir
                    }
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /*override fun onResume() {
        super.onResume()        // Adapter varsa ve güncelleme fonksiyonu çağrıldıysa, güncelleme işlemini gerçekleştir        adapter?.updateUserList()    }*/
}