package com.example.seall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.seall.ui.screens.MainScreen
import com.example.seall.ui.theme.SeallTheme
import com.example.seall.ui.viewmodel.OrderViewModel

class MainActivity : ComponentActivity() {

    private val orderViewModel: OrderViewModel by viewModels {
        OrderViewModel.provideFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var isDarkMode by rememberSaveable { mutableStateOf(systemDark) }

            SeallTheme(darkTheme = isDarkMode) {
                MainScreen(
                    viewModel = orderViewModel,
                    isDarkMode = isDarkMode,
                    onToggleTheme = { isDarkMode = it }
                )
            }
        }
    }
}
