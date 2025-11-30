package com.example.a20200135pc02moviles.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckApiService {
    @GET("deck/new/shuffle/")
    suspend fun createDeck(@Query("deck_count") deckCount: Int = 1): DeckResponse

    @GET("deck/{deck_id}/draw/")
    suspend fun drawCards(@Path("deck_id") deckId: String, @Query("count") count: Int): DrawResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://deckofcardsapi.com/api/"

    val service: DeckApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DeckApiService::class.java)
    }
}
