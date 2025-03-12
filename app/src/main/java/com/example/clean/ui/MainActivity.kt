package com.example.clean.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.clean.authentication.GoogleAuthClient
import com.example.clean.navigation.NavHostSetUp
import com.example.clean.ui.theme.CleanTheme

class MainActivity : ComponentActivity() {

    private val googleAuthClient by lazy {
        GoogleAuthClient(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CleanTheme {
                val navController = rememberNavController()

                NavHostSetUp(
                    navController = navController,
                    googleAuthClient = googleAuthClient
                )
            }
        }
    }
}
