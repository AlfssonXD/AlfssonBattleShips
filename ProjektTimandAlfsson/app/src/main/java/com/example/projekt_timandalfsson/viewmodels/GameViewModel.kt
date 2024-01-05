package com.example.projekt_timandalfsson.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekt_timandalfsson.BoardData
import com.example.projekt_timandalfsson.OrientationOfShip
import com.example.projekt_timandalfsson.ShipData
import io.garrit.android.multiplayer.ActionResult
import io.garrit.android.multiplayer.GameResult
import io.garrit.android.multiplayer.SupabaseCallback
import io.garrit.android.multiplayer.SupabaseService
import kotlinx.coroutines.launch

class GameViewModel : ViewModel(), SupabaseCallback {

    //ship objects
    private val _ship = mutableStateListOf<ShipData>()
    private var numOfShipPlaced = mutableIntStateOf(0)
    private val totNumOfShipsAvailable = 6
    private var playerShipDown = 0
    private var opponentShipDown = 0

    //board objects
    private val _playerBoard = mutableStateListOf<BoardData>()
    private val _opponentBoard = mutableStateListOf<BoardData>()
    private val boardSize: Int = 10 * 10


    private var latestMarkedBoardIndex = -1



    //ready checks
    var opponentReady = mutableStateOf(false)
    var playerReady = mutableStateOf(false)

    //winVariable
    var playerWinState = mutableStateOf(false)
    var gameOver = mutableStateOf(false)

    //turn checks
    var playerTurn = mutableStateOf(false)


    fun playerWon() {
        playerWinState.value = true
    }

    fun hitOrMiss(boardIndex: Int): Boolean {
        return playerBoard[boardIndex].hasShip()
    }

    fun isShipDown(boardIndex: Int): Boolean {
        return playerBoard[boardIndex].theShip?.isSunk() ?: false
    }

    fun isAllShipDown(): Boolean {
    return playerShipDown >= totNumOfShipsAvailable
    }

    override suspend fun playerReadyHandler() {
        viewModelScope.launch {  println("player ready handler called")
            //om både opponent och player är redo, gå till gamescreen
            opponentReady.value = true }
    }

    override suspend fun releaseTurnHandler() {
        println("release turn handler called")
        //när opponent klar med sin turn blir det players turn
        playerTurn.value = true
    }


    override suspend fun actionHandler(x: Int, y: Int) {
        println("action handler called")
        val boardIndex = convertCoordinateToIndex(x, y)
        playerBoard[boardIndex].markAsShot()
        if (hitOrMiss(boardIndex)) {
            playerBoard[boardIndex].theShip!!.takeDamage()
            viewModelScope.launch { SupabaseService.sendAnswer(ActionResult.HIT) }

            if (isShipDown(boardIndex)) { //check ifall den sjönk
                println("skepp nere")
                ++playerShipDown
                viewModelScope.launch { SupabaseService.sendAnswer(ActionResult.SUNK) }
            }
        }
        if (!hitOrMiss(boardIndex)) {
            viewModelScope.launch { SupabaseService.sendAnswer(ActionResult.MISS) }
        }
        if (isAllShipDown()) { //om alla skepp sjunkt
            viewModelScope.launch { SupabaseService.gameFinish(GameResult.WIN) }
            viewModelScope.launch { SupabaseService.leaveGame() }
            gameOver.value = true
        }
    }

    override suspend fun answerHandler(status: ActionResult) {
        println("answer handler called")
        if (status == ActionResult.MISS) {
            playerTurn.value = false
            viewModelScope.launch { SupabaseService.releaseTurn() }
        }
        if (status == ActionResult.HIT) {
            opponentBoard[latestMarkedBoardIndex].hasShip.value = true
        }
        if(status == ActionResult.SUNK) {
            ++opponentShipDown
        }
    }

    fun markTheseCoordinates(board: BoardData) {
        val x = board.getXCoordinate()
        val y = board.getYCoordinate()

        latestMarkedBoardIndex = convertCoordinateToIndex(x, y)
        viewModelScope.launch { SupabaseService.sendTurn(x, y) }
        opponentBoard[latestMarkedBoardIndex].markAsShot()
        println("mark coordinates called")
    }

    override suspend fun finishHandler(status: GameResult) {
        if (status == GameResult.WIN || status == GameResult.SURRENDER) {
            playerWinState.value = true
            gameOver.value = true
            viewModelScope.launch { SupabaseService.leaveGame() }
        }
        if (status == GameResult.LOSE) {
            playerWinState.value = false
            gameOver.value = true
            viewModelScope.launch { SupabaseService.leaveGame() }
        }
        if (status == GameResult.DRAW) {
            print("wtf how???????")
            gameOver.value = true
        }
    }

    val playerBoard: SnapshotStateList<BoardData>
        get() = _playerBoard

    val opponentBoard: SnapshotStateList<BoardData>
        get() = _opponentBoard

    val shipData: SnapshotStateList<ShipData>
        get() = _ship

    init {
        SupabaseService.callbackHandler = this
        playerTurn.value= (SupabaseService.player?.id == SupabaseService.currentGame?.player1?.id)

        _playerBoard.clear()
        _opponentBoard.clear()
        _ship.clear()
        val shipSizes = listOf(4 to 1, 3 to 1, 2 to 2, 1 to 2)
        val tempPlayerBoard = mutableListOf<BoardData>()
        val tempOpponentBoard = mutableListOf<BoardData>()
        val tempShip = mutableListOf<ShipData>()

        repeat(boardSize) { currIte ->
            tempPlayerBoard.add(BoardData(xCoordinate = currIte % 10, yCoordinate = currIte / 10))
            tempOpponentBoard.add(BoardData(xCoordinate = currIte % 10, yCoordinate = currIte / 10))
        }

        tempShip.addAll(shipSizes.flatMap { (size, count) ->
            List(count) { ShipData(size) }
        })

        _playerBoard.addAll(tempPlayerBoard)
        _opponentBoard.addAll(tempOpponentBoard)
        _ship.addAll(tempShip)
    }

    private fun orientationMultiplier(orientation: OrientationOfShip): Int {
        return when (orientation) {
            OrientationOfShip.HORIZONTALRIGHT -> 1
            OrientationOfShip.VERTICALDOWN -> 10
            OrientationOfShip.HORIZONTALLEFT -> -1
            OrientationOfShip.VERTICALUP -> -10
        }
    }

    fun removeShipFromBoard(ship: ShipData) {
        val boardIndexFromShip = ship.boardIndex

        repeat(ship.getSize()) { addToIndex ->
            playerBoard[boardIndexFromShip + (addToIndex * orientationMultiplier(ship.orientation))].removeShip()
        }
        ship.dePlaceShip()
        --numOfShipPlaced.value
    }

    //If allowed, ship will be placed at x & y coordinates and then return true to confirm its done
    fun requestToPlaceShip(board: BoardData, ship: ShipData): Boolean {
        val boardIndex = convertCoordinateToIndex(board.getXCoordinate(), board.getYCoordinate()) //Init the var by curr boardSquare
        val shipSize = ship.getSize()
        val orientation = ship.orientation

        if (isAllowedToPlaceShipHere(boardIndex, ship) && !ship.isPlaced()) {
            repeat(shipSize) { addToIndex ->
                playerBoard[boardIndex + (addToIndex * orientationMultiplier(orientation))].putShip(
                    ship
                )
            }
            ship.placeShip(orientation, boardIndex)
            ++numOfShipPlaced.value
            return true
        }
        return false
    }

    fun isAllowedToSelectShip(selectedShip: ShipData?, theShip: ShipData): Boolean {
        if (selectedShip != null || theShip.isPlaced()) {
            return false
        }
        return true
    }

    //help function for checking if allowed to place ship at the specific coordinates
    private fun isAllowedToPlaceShipHere(boardIndex: Int, ship: ShipData): Boolean {
        val (x, y) = convertIndexToCoordinate(boardIndex)

        if (!isInsideTheBoard(x, y, ship)) {
            return false
        }
        if (!isEnoughSpaceFromOtherShip(x, y, ship)) {
            return false
        }
        return true
    }

    fun convertIndexToCoordinate(boardIndex: Int): Pair<Int, Int> {
        val x = boardIndex % 10
        val y = boardIndex / 10
        return Pair(x, y)
    }

    fun convertCoordinateToIndex(x: Int, y: Int): Int {
        return x + (y * 10)
    }

    private fun isEnoughSpaceFromOtherShip(x: Int, y: Int, ship: ShipData): Boolean {
        val shipSize = ship.getSize()
        val orientation = ship.orientation

        if (isHorizontal(ship)) {
            repeat(shipSize) {
                val currentX = x + (it * orientationMultiplier(orientation))

                if (playerBoard[convertCoordinateToIndex(currentX, y)].hasShip() ||
                    isAdjacentCellOccupied(currentX, y - 1) ||
                    isAdjacentCellOccupied(currentX, y + 1)
                )
                    return false
            }
            if (isAdjacentCellOccupied(x - orientationMultiplier(orientation), y) ||
                isAdjacentCellOccupied(x + shipSize * orientationMultiplier(orientation), y)
            )
                return false
        }
        if (isVertical(ship)) {
            repeat(shipSize) {
                val currentY = y + (it * orientationMultiplier(orientation) / 10)

                if (playerBoard[convertCoordinateToIndex(x, currentY)].hasShip() ||
                    isAdjacentCellOccupied(x - 1, currentY) ||
                    isAdjacentCellOccupied(x + 1, currentY)
                )
                    return false
            }
            if (isAdjacentCellOccupied(x, y - (orientationMultiplier(orientation) / 10)) ||
                isAdjacentCellOccupied(
                    x,
                    y + shipSize * (orientationMultiplier(orientation) / 10)
                )
            )
                return false
        }
        return true
    }

    private fun isAdjacentCellOccupied(x: Int, y: Int): Boolean {
        return (x in 0..9 && y in 0..9) && playerBoard[convertCoordinateToIndex(x, y)].hasShip()
    }

    //Adding length of the ship to the coordinate. If x is inside then rest of ship will be inside as well.
    private fun isInsideTheBoard(
        xFirstCoordinate: Int,
        yFirstCoordinate: Int,
        ship: ShipData
    ): Boolean {
        val shipSize = ship.getSize()
        val orientation = ship.orientation
        if (isHorizontal(ship)) {
            return xFirstCoordinate + ((shipSize - 1) * orientationMultiplier(orientation)) in 0..9
        }
        if (isVertical(ship)) {
            return yFirstCoordinate + ((shipSize - 1) * (orientationMultiplier(orientation) / 10)) in 0..9
        }
        return false
    }

    //Orientation of the ships that is TO BE placed //
    fun toggleOrientation(ship: ShipData?) {
        val currOrientation = ship!!.orientation
        ship.orientation = when (currOrientation) {
            OrientationOfShip.HORIZONTALRIGHT -> OrientationOfShip.VERTICALDOWN
            OrientationOfShip.VERTICALDOWN -> OrientationOfShip.HORIZONTALLEFT
            OrientationOfShip.HORIZONTALLEFT -> OrientationOfShip.VERTICALUP
            OrientationOfShip.VERTICALUP -> OrientationOfShip.HORIZONTALRIGHT
        }
    }

    fun isHorizontal(ship: ShipData): Boolean {
        return (ship.orientation == OrientationOfShip.HORIZONTALLEFT ||
                ship.orientation == OrientationOfShip.HORIZONTALRIGHT)
    }

    fun isVertical(ship: ShipData): Boolean {
        return (ship.orientation == OrientationOfShip.VERTICALUP ||
                ship.orientation == OrientationOfShip.VERTICALDOWN)
    }

    //if all ships is placed then return true else false
    fun isAllShipsPlaced(): Boolean {
        return numOfShipPlaced.value >= totNumOfShipsAvailable
    }


    fun setPlayerReady() {
        playerReady.value = true
        viewModelScope.launch { SupabaseService.playerReady() }
    }

}