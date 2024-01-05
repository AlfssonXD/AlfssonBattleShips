package com.example.projekt_timandalfsson.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projekt_timandalfsson.R
import com.example.projekt_timandalfsson.viewmodels.HomeViewModel
import com.example.projekt_timandalfsson.viewmodels.LobbyViewModel
import io.garrit.android.multiplayer.Game
import io.garrit.android.multiplayer.Player

@Composable
fun Lobbyscreen(
    navController: NavController,
    LobbyViewModel: LobbyViewModel = viewModel(),
    homeViewModel: HomeViewModel
) {
    LaunchedEffect(key1 = LobbyViewModel.GameAccepted.value) {
        if (LobbyViewModel.GameAccepted.value) {
            navController.navigate(Screens.GameScreen.route)
        }
    }

    var showList1 by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF4CAF50)) // Green color
        ) {
            Image(
                painter = painterResource(id = R.drawable.fat2),
                contentDescription = "win image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.Transparent)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Transparent)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Game Lobby",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp),
                        style = TextStyle(fontSize = 24.sp, color = Color.White)
                    )

                    Button(
                        onClick = { showList1 = !showList1 },
                        colors = ButtonDefaults.buttonColors(Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = if (showList1) "Show invites" else "Show Players")
                    }

                    if (showList1) {
                        LazyColumn() {
                            items(LobbyViewModel.playerlist) {
                                PlayerView(it, navController)
                            }
                        }
                    } else {
                        LazyColumn() {
                            items(LobbyViewModel.invitelist) {
                                GameView(it, navController)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun PlayerView(
    player: Player,
    navController: NavController,
    LobbyViewModel: LobbyViewModel = viewModel()
) {
    var inviteButtonText by remember { mutableStateOf("Invite") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
        ) {
            Text(text = player.name, style = TextStyle(fontSize = 20.sp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
        ) {
            Button(
                onClick = {
                    LobbyViewModel.invite(player = player)
                    inviteButtonText = "Invited!"
                },
                colors = ButtonDefaults.buttonColors(Color.Black),
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(text = inviteButtonText, style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}

@Composable
fun GameView(
    game: Game,
    navController: NavController,
    LobbyViewModel: LobbyViewModel = viewModel()
) {
    var showButtons by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                showButtons = !showButtons
            }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
        ) {
            Text(text = game.player1.name, style = TextStyle(fontSize = 20.sp))
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(
        ) {
            if (showButtons) {
                Button(
                    onClick = {
                        LobbyViewModel.accept(game)
                        navController.navigate(Screens.GameScreen.route)
                    },
                    modifier = Modifier
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(Color.Green)
                ) {
                    Text(text = "Accept", style = TextStyle(fontSize = 20.sp, color = Color.White))
                }
                Button(
                    onClick = { LobbyViewModel.decline(game) },
                    modifier = Modifier
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Text(text = "Decline", style = TextStyle(fontSize = 20.sp, color = Color.White))
                }
            }
        }
    }
}
