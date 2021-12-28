package com.sg.bookapp50.models

import com.google.firebase.Timestamp

data class Cat(
    var id:String="",
    val categoryName: String="",
    val timestamp: Timestamp?,
    val uid: String=""
)