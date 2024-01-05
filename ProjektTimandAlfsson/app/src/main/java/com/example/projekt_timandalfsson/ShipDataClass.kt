package com.example.projekt_timandalfsson

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

data class ShipData(
    private var size: Int,
    var icon: ImageVector = Icons.Default.Home,
    var xCoordinate: MutableState<Int> = mutableStateOf(-1),
    var yCoordinate: MutableState<Int> = mutableStateOf(-1),
    var hitsTaken: MutableState<Int> = mutableStateOf(0),
    var isPlaced: MutableState<Boolean> = mutableStateOf(false),
    var isSelected: MutableState<Boolean> = mutableStateOf(false),
    var orientation: OrientationOfShip = OrientationOfShip.HORIZONTALRIGHT,
    var boardIndex: Int = -1

) {
    fun selectShip() {
        isSelected.value = true
    }

    fun deSelectShip() {
        isSelected.value = false
    }

    fun isSelected(): Boolean {
        return isSelected.value
    }

    fun setSize(size: Int) {
        this.size = size
    }

    fun getSize(): Int {
        return this.size
    }

    fun takeDamage() {
        ++this.hitsTaken.value
    }

    fun isSunk(): Boolean {
        return this.hitsTaken.value >= this.size
    }

    fun getXCoordinate(): Int {
        return xCoordinate.value
    }
    fun getYCoordinate(): Int {
        return yCoordinate.value
    }

    fun placeShip(orientation: OrientationOfShip, boardIndex: Int) {
        isPlaced.value = true
        this.orientation = orientation
        this.boardIndex = boardIndex
    }

    fun dePlaceShip() {
        isPlaced.value = false
    }

    fun isPlaced(): Boolean {
        return isPlaced.value
    }
}
