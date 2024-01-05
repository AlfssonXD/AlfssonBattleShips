package com.example.projekt_timandalfsson.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.projekt_timandalfsson.R
import com.example.projekt_timandalfsson.viewmodels.GameViewModel
import com.example.projekt_timandalfsson.viewmodels.HomeViewModel


@Composable
fun WinScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color(0xFF4CAF50)) // Green color
        ) {
            Image(
                painter = painterResource(id = R.drawable.winimg1),
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
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DrawOutlinedText(
                        text = "             You Won!!!!",
                        color = Color.White,
                        outlineColor = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.Transparent)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom, // Align at the top
                        horizontalAlignment = Alignment.CenterHorizontally // Align at the center horizontally
                    ) {
                        Button(
                            modifier = Modifier
                                .height(60.dp)
                                .padding(8.dp),
                            onClick = {
                                navController.navigate(Screens.HomeScreen.route)
                            }
                        ) {
                            Text(
                                text = "Return Home",
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

