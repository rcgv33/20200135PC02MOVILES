package com.example.a20200135pc02moviles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.a20200135pc02moviles.ui.GameScreen
import com.example.a20200135pc02moviles.ui.theme._20200135PC02MOVILESTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            _20200135PC02MOVILESTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pass the padding to the screen to respect system bars
                    androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                        GameScreen()
                    }
                }
            }
        }
    }
}
