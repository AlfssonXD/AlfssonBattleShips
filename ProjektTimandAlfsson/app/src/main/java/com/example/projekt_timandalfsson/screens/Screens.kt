package com.example.projekt_timandalfsson.screens

sealed class Screens(val route: String) {
    object HomeScreen : Screens(route = "HomeScreen")
    object LobbyScreen : Screens(route = "LobbyScreen")
    object GameScreen : Screens(route = "GameScreen")
    object WinScreen : Screens(route = "WinScreen")
    object LoseScreen : Screens(route = "LoseScreen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")

            }
        }
    }
}


