package com.example.cardadvisor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cardadvisor.ui.camera.CameraScreen
import com.example.cardadvisor.ui.cards.AddCardScreen
import com.example.cardadvisor.ui.cards.CardsScreen

object Routes {
    const val CAMERA = "camera"
    const val CARDS = "cards"
    const val ADD_CARD = "add_card"
    const val EDIT_CARD = "edit_card/{cardId}"
    fun editCard(cardId: Long) = "edit_card/$cardId"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                CardAdvisorApp()
            }
        }
    }
}

@Composable
fun CardAdvisorApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNav(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.CAMERA,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.CAMERA) {
                CameraScreen(
                    onNavigateToCards = { navController.navigate(Routes.CARDS) }
                )
            }
            composable(Routes.CARDS) {
                CardsScreen(
                    onAddCard = { navController.navigate(Routes.ADD_CARD) },
                    onEditCard = { cardId -> navController.navigate(Routes.editCard(cardId)) }
                )
            }
            composable(Routes.ADD_CARD) {
                AddCardScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Routes.EDIT_CARD,
                arguments = listOf(navArgument("cardId") { type = NavType.LongType })
            ) { backStack ->
                val cardId = backStack.arguments?.getLong("cardId")
                AddCardScreen(
                    onBack = { navController.popBackStack() },
                    cardId = cardId
                )
            }
        }
    }
}

@Composable
private fun BottomNav(navController: NavHostController) {
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = current == Routes.CAMERA,
            onClick = {
                navController.navigate(Routes.CAMERA) {
                    popUpTo(Routes.CAMERA) { inclusive = true }
                }
            },
            icon = { Icon(Icons.Default.CameraAlt, contentDescription = "Scan") },
            label = { Text("Scan") }
        )
        NavigationBarItem(
            selected = current == Routes.CARDS || current == Routes.ADD_CARD || current == Routes.EDIT_CARD,
            onClick = {
                navController.navigate(Routes.CARDS) {
                    popUpTo(Routes.CAMERA)
                }
            },
            icon = { Icon(Icons.Default.CreditCard, contentDescription = "Cards") },
            label = { Text("My Cards") }
        )
    }
}
