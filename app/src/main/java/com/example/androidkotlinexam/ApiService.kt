package com.example.androidkotlinexam

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("https://run.mocky.io/v3/8707c9dc-a49d-4fe6-95d9-68fd5daad185")
    fun submitFormData(@Body formData: FormData): Call<ResponseBody>
}