package com.mad411.habittracker.network

import retrofit2.http.GET

//this is the interface retrofit turns into an actual http client at runtime
// we dont implement it, retrofit does that for us

interface QuoteApiService {

    // suspend means this function can only be called from a coroutine
    // retrofit handles running it on a background thread automatically
    //GET hits the random endpoint and returns a list (the api always gives us a list of 1)
    @GET("random")
    suspend fun getRandomQuote(): List<Quote>
}
