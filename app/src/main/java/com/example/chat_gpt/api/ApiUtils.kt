package com.example.chat_gpt.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object ApiUtils {

    fun getApiInterface(): ApiInterface {
       return Retrofit
           .Builder()
           .baseUrl("https://api.openai.com/")
           .addConverterFactory(GsonConverterFactory.create())
           .build()
           .create(ApiInterface::class.java)
    }
}