package com.example.a20200135pc02moviles.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a20200135pc02moviles.data.Card
import com.example.a20200135pc02moviles.data.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class GameUiState {
    object Idle : GameUiState()
    object Loading : GameUiState()
    data class Success(
        val cards: List<Card>,
        val playerScore: Int,
        val machineScore: Int,
        val resultMessage: String
    ) : GameUiState()
    data class Error(val message: String) : GameUiState()
}

class GameViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Idle)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var currentDeckId: String? = null

    init {
        initializeDeck()
    }

    private fun initializeDeck() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.service.createDeck()
                if (response.success) {
                    currentDeckId = response.deckId
                } else {
                    _uiState.value = GameUiState.Error("Failed to create deck")
                }
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error("Error initializing deck: ${e.message}")
            }
        }
    }

    fun playGame(cardCount: Int) {
        if (currentDeckId == null) {
            initializeDeck() // Try to re-initialize if failed previously or not ready
            // If still null after this async call, we might have an issue, but for now let's assume it works or the user retries.
            // Actually, we should probably wait or handle the case where deck is not ready.
            // For simplicity in this exam context, let's just proceed and check inside the coroutine.
        }

        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            try {
                if (currentDeckId == null) {
                    // Attempt synchronous-like or just wait for the previous init?
                    // Let's just try to create one now.
                    val deckResponse = RetrofitClient.service.createDeck()
                    if (deckResponse.success) {
                        currentDeckId = deckResponse.deckId
                    } else {
                        _uiState.value = GameUiState.Error("Could not create deck")
                        return@launch
                    }
                }

                // Draw cards
                val drawResponse = RetrofitClient.service.drawCards(currentDeckId!!, cardCount)
                if (drawResponse.success) {
                    val cards = drawResponse.cards
                    val playerScore = calculateScore(cards)
                    val machineScore = (16..21).random()
                    val result = determineWinner(playerScore, machineScore)

                    _uiState.value = GameUiState.Success(
                        cards = cards,
                        playerScore = playerScore,
                        machineScore = machineScore,
                        resultMessage = result
                    )
                } else {
                    _uiState.value = GameUiState.Error("Failed to draw cards")
                }

            } catch (e: Exception) {
                _uiState.value = GameUiState.Error("Network error: ${e.message}")
            }
        }
    }

    private fun calculateScore(cards: List<Card>): Int {
        var score = 0
        for (card in cards) {
            score += when (card.value) {
                "ACE" -> 11
                "KING", "QUEEN", "JACK", "10" -> 10
                else -> card.value.toIntOrNull() ?: 0
            }
        }
        return score
    }

    private fun determineWinner(playerScore: Int, machineScore: Int): String {
        return when {
            playerScore > 21 -> "Perdiste (Te pasaste de 21)"
            playerScore > machineScore -> "Ganaste (Tu puntaje es mayor)"
            playerScore < machineScore -> "Perdiste (La máquina tiene mayor puntaje)"
            else -> "Empate"
        }
    }
    
    fun resetGame() {
        _uiState.value = GameUiState.Idle
        // Optionally reshuffle or create new deck, but "currentDeckId" is sufficient if we just want to play again.
        // The requirements don't specify if we need a fresh deck every hand or just draw from the remaining.
        // "Endpoint 1 (Iniciar) ... Endpoint 2 (Pedir)". 
        // Usually Blackjack keeps the deck, but for a simple "Game" button, 
        // we might want to just ensure we have cards.
        // If we run out of cards, the API handles it or we create a new deck.
        // For now, let's keep the deckId.
    }
}
