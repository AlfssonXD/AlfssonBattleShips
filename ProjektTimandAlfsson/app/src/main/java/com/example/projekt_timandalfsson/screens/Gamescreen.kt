package com.example.projekt_timandalfsson.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.projekt_timandalfsson.BoardData
import com.example.projekt_timandalfsson.ShipData
import com.example.projekt_timandalfsson.viewmodels.GameViewModel

@Composable
fun GameScreen(
    gameViewModel: GameViewModel,
    navController: NavController,
) {

    val playerBoard = gameViewModel.playerBoard
    val opponentBoard = gameViewModel.opponentBoard
    val ship = gameViewModel.shipData
    var currentBoard: SnapshotStateList<BoardData>

    var selectedShip: ShipData? by remember { mutableStateOf(null) }


    //checks for player ready
    var allPlayersready by remember {
        mutableStateOf(false)
    }

    if (gameViewModel.playerReady.value && gameViewModel.opponentReady.value) {
        allPlayersready = true
    }

    //checks for player turn
    if (gameViewModel.playerTurn.value) {
        //visa opponent board
        currentBoard = opponentBoard

    } else {
        // visa player board
        currentBoard = playerBoard
    }

    if (gameViewModel.gameOver.value) {
        if (gameViewModel.playerWinState.value) {
            navController.navigate(Screens.WinScreen.route)
        } else {
            navController.navigate(Screens.LoseScreen.route)
        }
    }

    Column(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        //The view for preGame is created here
        if (!allPlayersready) {
            Text(
                text = "   STRATEGY PANEL   ",
                fontSize = 30.sp,
                color = Color.Black,
                modifier = Modifier.background(Color.Gray, RoundedCornerShape(25))
            )
            Spacer(modifier = Modifier.height(30.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(10),
                userScrollEnabled = false,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                items(playerBoard) { itBoard ->
                    PreGameBoardView(itBoard, onClick = {
                        if (!gameViewModel.playerReady.value) {
                            if (itBoard.hasShip()) {
                                selectedShip?.let { it.deSelectShip() }
                                if(selectedShip == itBoard.theShip) {
                                    selectedShip!!.deSelectShip()
                                    selectedShip = null
                                }
                                else {
                                    selectedShip = itBoard.theShip
                                    selectedShip!!.selectShip()
                                }
                            } else {
                                selectedShip?.let {
                                    gameViewModel.requestToPlaceShip(itBoard, it)
                                }
                            }
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.size(30.dp))
            ship.forEach() { itShip ->
                Spacer(modifier = Modifier.size(5.dp))
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(35.dp),
                    userScrollEnabled = true,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    items(itShip.getSize()) {
                        PreGameShipView(ship = itShip, onClick = {
                            if(!itShip.isPlaced()) {
                                selectedShip?.let { it.deSelectShip() }
                                itShip.selectShip()
                                selectedShip = itShip
                            }
                        })
                    }
                }
            }
            if (gameViewModel.isAllShipsPlaced() && !gameViewModel.playerReady.value && selectedShip == null) {
                Button(
                    onClick = { gameViewModel.setPlayerReady() },
                    colors = ButtonDefaults.buttonColors(Color.Green)
                ) {
                    Text(
                        text = "  READY  ", fontSize = 30.sp, color = Color.Black
                    )
                }
            } else {
                OutlinedIconButton(
                    onClick = {
                        selectedShip?.let { itShip ->
                            if (itShip.isPlaced()) {
                                while (!gameViewModel.requestToPlaceShip(
                                        playerBoard[itShip.boardIndex],
                                        itShip
                                    )
                                ) {
                                    if(itShip.isPlaced()) {
                                        gameViewModel.removeShipFromBoard(itShip)
                                    }
                                    gameViewModel.toggleOrientation(itShip)
                                }
                            }
                        }
                    },
                    colors = IconButtonDefaults.outlinedIconButtonColors(Color.Gray),
                    shape = RoundedCornerShape(25)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rotate the ship",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            //GameView is created here
        } else {
            if (gameViewModel.playerTurn.value) {
                Text(
                    fontSize = 30.sp,
                    text = "    ATTACK YOUR OPPONENT!   ",
                    color = Color.Black,
                    modifier = Modifier.background(Color.Red, RoundedCornerShape(25))
                )
            } else {
                Text(
                    fontSize = 30.sp,
                    text = "    YOUR OPPONENT'S TURN!   ",
                    color = Color.Black,
                    modifier = Modifier.background(Color.Blue, RoundedCornerShape(25))
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(10),
                userScrollEnabled = false,
                modifier = Modifier.padding(5.dp)
            ) {
                items(currentBoard) {
                    when (currentBoard) {
                        playerBoard -> playerGameBoardView(it)

                        else -> opponentGameBoardView(it,
                            onClick = { gameViewModel.markTheseCoordinates(it) })
                    }
                }
            }
        }
    }
}

@Composable
fun PreGameShipView(ship: ShipData, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f, false),
        colors = ButtonDefaults.buttonColors(
            containerColor = when {
                ship.isPlaced() -> Color.Transparent
                else -> when {
                    ship.isSelected() -> Color.Yellow
                    else -> Color.Blue
                }
            }
        ),
        shape = RoundedCornerShape(25)
    ) {}
}


@Composable
fun PreGameBoardView(theBoardSquare: BoardData, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f, true),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (theBoardSquare.hasShip()) {
                if (theBoardSquare.theShip!!.isSelected()) {
                    Color.Yellow
                } else Color.Blue
            } else Color.Gray

        ),
        shape = RoundedCornerShape(25)
    ) {}
}

//This will show when it's the opponent turn to play
@Composable
fun playerGameBoardView(playerBoard: BoardData) {
    OutlinedIconButton(
        onClick = {},
        modifier = Modifier.aspectRatio(1f, true),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = when {
                playerBoard.hasShip() -> Color.Blue
                else -> Color.Gray
            }
        ),
        shape = RoundedCornerShape(25)
    ) {
        when {
            playerBoard.hasBeenShot() -> Icon(
                imageVector = Icons.Default.Close,
                tint = when {
                    playerBoard.hasShip() -> Color.Red
                    else -> Color.Blue
                }, contentDescription = "Will display a symbol if it has been shot"
            )
        }
    }
}

//This will show when it's the player turn to play
@Composable
fun opponentGameBoardView(opponentBoard: BoardData, onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        modifier = Modifier.aspectRatio(1f, false),
        colors = IconButtonDefaults.outlinedIconButtonColors(
            containerColor = when {
                opponentBoard.hasShip() -> Color.Red
                else -> Color.Gray
            }
        ),
        shape = RoundedCornerShape(25)
    ) {
        when {
            opponentBoard.hasBeenShot() -> Icon(
                imageVector = Icons.Default.Close,
                tint = when {
                    opponentBoard.hasShip() -> Color.Blue
                    else -> Color.Red
                },
                contentDescription = "Will display a symbol if it has been shot"
            )
        }
    }
}