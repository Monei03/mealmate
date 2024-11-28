package com.example.mealmate.network


import com.example.mealmate.models.Amount
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://api.spoonacular.com/"

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(Amount::class.java, AmountDeserializer())
        .create()

    val apiService: SpoonacularApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(SpoonacularApiService::class.java)
    }
}
