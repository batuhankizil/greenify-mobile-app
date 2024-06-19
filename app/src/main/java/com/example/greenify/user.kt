package com.example.greenify

import android.os.Parcel
import android.os.Parcelable

data class User(val name: String, val score: Int)
data class UserCompetent(val userName: String?, val location: String?, val userId: String, val surname: String?)