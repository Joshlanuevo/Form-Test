package com.example.androidkotlinexam

data class FormData(
    val fullName: String,
    val email: String,
    val mobile: String,
    val dob: String,
    val gender: String
)

data class ApiResponse(
    val message: String
)
