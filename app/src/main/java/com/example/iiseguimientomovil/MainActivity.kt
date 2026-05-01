package com.example.iiseguimientomovil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.iiseguimientomovil.ui.navigation.AppNavigation
import com.example.iiseguimientomovil.ui.theme.IISeguimientoMovilTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IISeguimientoMovilTheme {
                AppNavigation()
            }
        }
    }
}