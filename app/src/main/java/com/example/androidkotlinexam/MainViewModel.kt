package com.example.androidkotlinexam

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel : ViewModel() {

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    fun submitFormData(formData: FormData) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.submitFormData(formData)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string() ?: "Empty response"
                    Log.d("test-test", responseBody)
                    _responseMessage.postValue(responseBody)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Bad request"
                        401 -> "Unauthorized"
                        else -> "Error: ${response.code()}"
                    }
                    Log.e("test-test", errorMessage)
                    _responseMessage.postValue(errorMessage)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _responseMessage.postValue("API call failed: ${t.message}")
            }
        })
    }
}
