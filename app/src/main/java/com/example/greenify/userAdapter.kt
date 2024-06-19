package com.example.greenify

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(private val userList: List<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_rank, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userName.text = currentUser.name
        holder.userScore.text = "Puan : ${currentUser.score}"
        holder.userRank.text = (position + 1).toString() // Sıra numarasını atama

    }


    override fun getItemCount() = userList.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
        val userRank: TextView = itemView.findViewById(R.id.userRank) // Sıra numarasını gösterecek olan metin görünümü
    }

}
