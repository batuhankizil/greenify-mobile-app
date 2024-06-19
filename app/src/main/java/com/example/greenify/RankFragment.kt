package com.example.greenify

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class RankFragment : Fragment() {

    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private val userList = mutableListOf<User>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_rank, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = UserAdapter(userList)
        recyclerView.adapter = userAdapter

        fetchUserScores()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUserScores() {
        val db = FirebaseFirestore.getInstance()
        val usersRef = db.collection("users")

        usersRef
            .orderBy("score", Query.Direction.DESCENDING) // Puanlara göre sıralama
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val name = document.getString("userName")
                    val score = document.getLong("score")?.toInt()
                    if (name != null && score != null) {
                        val user = User(name, score)
                        userList.add(user)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Hata durumunda buraya düşer
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    companion object {
        private const val TAG = "RankFragment"
    }
}