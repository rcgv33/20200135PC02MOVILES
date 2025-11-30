package com.example.a20200135pc02moviles.data

import com.google.gson.annotations.SerializedName

data class DeckResponse(
    @SerializedName("deck_id") val deckId: String,
    val success: Boolean,
    val remaining: Int,
    val shuffled: Boolean
)

data class DrawResponse(
    val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    val cards: List<Card>,
    val remaining: Int
)

data class Card(
    val code: String,
    val image: String,
    val value: String,
    val suit: String
)
