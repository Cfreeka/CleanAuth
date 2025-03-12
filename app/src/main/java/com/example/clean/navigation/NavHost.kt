package com.example.clean.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.clean.authentication.GoogleAuthClient

@Composable
fun NavHostSetUp(
    navController: NavHostController,
    googleAuthClient: GoogleAuthClient
) {
    NavHost(
        navController = navController,
        startDestination = "signIn",
        enterTransition = {
            fadeIn() + slideInHorizontally()
        },
        exitTransition = {
            fadeOut() + slideOutHorizontally()
        }
    ) {
        composable("signIn") {
            SignInScreen(
                navController = navController,
                googleAuthClient = googleAuthClient
            )
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                userData = googleAuthClient.getCurrentUser(),
                googleAuthClient = googleAuthClient
            )
        }
        composable("signUp") {
            SignUpScreen(
                navController = navController,
                googleAuthClient = googleAuthClient
            )
        }
    }
}