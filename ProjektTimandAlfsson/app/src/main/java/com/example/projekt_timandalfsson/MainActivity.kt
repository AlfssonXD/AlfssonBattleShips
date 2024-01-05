package com.example.projekt_timandalfsson

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projekt_timandalfsson.screens.GameScreen
import com.example.projekt_timandalfsson.screens.HomeScreen
import com.example.projekt_timandalfsson.screens.Lobbyscreen
import com.example.projekt_timandalfsson.screens.LoseScreen
import com.example.projekt_timandalfsson.screens.Screens
import com.example.projekt_timandalfsson.screens.WinScreen
import com.example.projekt_timandalfsson.viewmodels.GameViewModel
import com.example.projekt_timandalfsson.viewmodels.HomeViewModel
import com.example.projekt_timandalfsson.viewmodels.LobbyViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = Screens.HomeScreen.route) {
                composable(route = Screens.HomeScreen.route) {
                    HomeScreen(navController = navController)
                }
                composable(route = Screens.LobbyScreen.route) {
                    val lobbyViewModel: LobbyViewModel = viewModel()
                    Lobbyscreen(navController = navController, homeViewModel = HomeViewModel())
                }
                composable(route = Screens.WinScreen.route){
                    WinScreen(navController = navController)
                }
                composable(route = Screens.LoseScreen.route){
                    LoseScreen(navController = navController)
                }
                composable(route = Screens.GameScreen.route) {
                    val gameViewModel: GameViewModel = viewModel()
                    GameScreen(gameViewModel = gameViewModel, navController)
                }
            }
        }
    }
}
