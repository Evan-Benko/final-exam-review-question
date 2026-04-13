package com.mad411.habittracker.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// singleton retrofit instance
// using object means theres only ever one, we dont wanna rebuild this on every call
// this is the pattern we used in class with the task manager app

object RetrofitInstance {

    //base url for zenquotes, trailing slash is important for retrofit
    private const val BASE_URL = "https://zenquotes.io/api/"

    //lazy means we only build retrofit the first time someones asks for api
    //saves memory if the user never opens the quote screen
    val api: QuoteApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            //gson converter turns the json response into our Quote data class
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            //create() is what generates the actual implementation of our interface
            .create(QuoteApiService::class.java)
    }
}
