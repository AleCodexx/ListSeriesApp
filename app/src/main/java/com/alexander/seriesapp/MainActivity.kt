package com.alexander.seriesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexander.seriesapp.ui.LoginScreen
import com.alexander.seriesapp.ui.RegisterScreen
import com.alexander.seriesapp.ui.SeriesScreen
import com.alexander.seriesapp.data.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val sessionManager = SessionManager(this)


            val startDestination = if (sessionManager.getUser() != null) "series" else "login"

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate("series") {
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onRegisterClick = {
                            navController.navigate("register")
                        }
                    )
                }

                composable("register") {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.navigate("series") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }

                composable("series") {
                    SeriesScreen(
                        onLogout = {
                            sessionManager.clearUser()
                            navController.navigate("login") {
                                popUpTo("series") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}
