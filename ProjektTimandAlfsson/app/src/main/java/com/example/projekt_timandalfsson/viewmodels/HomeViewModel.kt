package com.example.projekt_timandalfsson.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.garrit.android.multiplayer.Player
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    fun createPlayerAndJoin(name: String) {
        viewModelScope.launch { SupabaseService.joinLobby(Player(name = name)) }
    }
}