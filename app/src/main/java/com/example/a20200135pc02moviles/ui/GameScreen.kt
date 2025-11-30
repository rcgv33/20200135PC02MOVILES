package com.example.a20200135pc02moviles.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a20200135pc02moviles.ui.viewmodel.GameUiState
import com.example.a20200135pc02moviles.ui.viewmodel.GameViewModel

@Composable
fun GameScreen(viewModel: GameViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var cardCountText by remember { mutableStateOf("2") }
    var isErrorInput by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Blackjack - 21", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = cardCountText,
            onValueChange = {
                cardCountText = it
                // Validate input immediately or on button press. 
                // Let's do basic validation here to reset error if valid number typed.
                val count = it.toIntOrNull()
                if (count != null && count in 2..5) {
                    isErrorInput = false
                }
            },
            label = { Text("Número de cartas (2-5)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = isErrorInput,
            singleLine = true
        )
        if (isErrorInput) {
            Text(text = "Debe ser entre 2 y 5", color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val count = cardCountText.toIntOrNull()
                if (count != null && count in 2..5) {
                    isErrorInput = false
                    viewModel.playGame(count)
                } else {
                    isErrorInput = true
                }
            },
            enabled = uiState !is GameUiState.Loading
        ) {
            Text(if (uiState is GameUiState.Loading) "Cargando..." else "Jugar")
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (val state = uiState) {
            is GameUiState.Loading -> {
                CircularProgressIndicator()
            }
            is GameUiState.Success -> {
                Text(text = "Cartas del Jugador:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(state.cards) { card ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(card.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = "${card.value} of ${card.suit}",
                            modifier = Modifier
                                .height(150.dp)
                                .width(100.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(text = "Puntaje Jugador: ${state.playerScore}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Puntaje Máquina: ${state.machineScore}", style = MaterialTheme.typography.bodyLarge)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = state.resultMessage,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (state.resultMessage.contains("Ganaste")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.resetGame() }) {
                    Text("Reiniciar")
                }
            }
            is GameUiState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.resetGame() }) {
                    Text("Intentar de nuevo")
                }
            }
            is GameUiState.Idle -> {
                Text("Ingresa cuántas cartas quieres y presiona Jugar")
            }
        }
    }
}
