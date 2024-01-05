package com.example.projekt_timandalfsson

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class BoardData(
    private val xCoordinate: Int,
    private val yCoordinate: Int,
    var hasShip: MutableState<Boolean> = mutableStateOf(false), //True when a ship is placed in the square
    var theShip: ShipData? = null, //Contains ship if it has, else value will be null
    var isShot: MutableState<Boolean> = mutableStateOf(false)
) {

    fun putShip(ship: ShipData) {
        theShip = ship
        hasShip.value = true
    }

    fun hasShip(): Boolean {
        return theShip != null || hasShip.value
    }

    fun hasBeenShot(): Boolean {
        return isShot.value
    }

    fun removeShip() {
        theShip = null
        hasShip.value = false
    }

    fun markAsShot() {
        isShot.value = true
    }

    fun getXCoordinate(): Int {
        return xCoordinate
    }

    fun getYCoordinate(): Int {
        return yCoordinate
    }
}
