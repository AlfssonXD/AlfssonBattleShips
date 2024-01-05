package com.example.projekt_timandalfsson.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.ServerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LobbyViewModel : ViewModel() {
    var playerlist = SupabaseService.users
    var invitelist = SupabaseService.games
    var GameAccepted = mutableStateOf(false)




    fun invite(player: Player) {
        viewModelScope.launch {
            SupabaseService.invite(player)
        }
    }

    fun accept(game: Game) {
        viewModelScope.launch { SupabaseService.acceptInvite(game = game) }
    }

    fun decline(game: Game) {
        viewModelScope.launch { SupabaseService.declineInvite(game = game) }
    }

    init {
        observeServerState()
    }
    //https://chat.openai.com/share/2346f645-8d6d-4b93-bfde-2d8591ed0acf
    private fun observeServerState() {
        viewModelScope.launch {
            SupabaseService.observeServerState { newState ->
                // Handle the server state change here in LobbyViewModel
                println("Server State changed to: $newState")

                // You can perform actions based on the newState
                when (newState) {
                    ServerState.LOADING_GAME -> {
                        GameAccepted.value = true
                    }
                    // Handle other server states as needed
                    else -> {
                    }
                }
            }
        }
    }
}

