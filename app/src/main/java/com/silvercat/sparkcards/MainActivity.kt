package com.silvercat.sparkcards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.silvercat.sparkcards.ui.navigation.AppNavHost
import com.silvercat.sparkcards.ui.theme.SparkCardsTheme

class MainActivity : ComponentActivity() {

    private val container get() = (application as SparkApplication).container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SparkCardsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(container = container)
                }
            }
        }
    }
}
