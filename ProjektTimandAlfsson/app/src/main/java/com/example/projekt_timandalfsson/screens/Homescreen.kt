package com.example.projekt_timandalfsson.screens

import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.projekt_timandalfsson.R
import com.example.projekt_timandalfsson.viewmodels.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeViewModel = viewModel()) {
    var text by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Yellow)
    ) {
        Box(modifier = Modifier.weight(1f)) {
            Image(
                painter = painterResource(id = R.drawable.img),
                contentDescription = "meme",
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
                        text = "Tim and Alfsson presents:",
                        color = Color.White,
                        outlineColor = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                    DrawOutlinedText(
                        text = "         BATTLESHIPS",
                        color = Color.White,
                        outlineColor = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .background(color = Color.Gray)
                    ) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = {
                                text = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = Color.Transparent)
                        )
                    }

                    if (text.isNotBlank()) {
                        Button(
                            onClick = {
                                homeViewModel.createPlayerAndJoin(text)
                                navController.navigate(Screens.LobbyScreen.route)
                            },
                            colors = ButtonDefaults.buttonColors(Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(color = Color.Transparent)
                        ) {
                            Text(text = "To lobby")
                        }
                    }
                }
            }
        }
    }
}

//following function made with help from friend in grade above
@Composable
fun DrawOutlinedText(
    text: String,
    color: Color,
    outlineColor: Color,
    modifier: Modifier = Modifier
) {
    val outlineWidth = 2.dp

    Text(
        text = text,
        color = outlineColor,
        modifier = modifier
            .drawWithContent {
                drawIntoCanvas { canvas ->
                    val paint = Paint().asFrameworkPaint()

                    paint.textSize = 30.sp.toPx()
                    paint.color = outlineColor.toArgb()
                    paint.typeface = Typeface.DEFAULT_BOLD

                    canvas.nativeCanvas.drawText(
                        text,
                        0f,
                        paint.fontSpacing,
                        paint
                    )

                    paint.textSize = 30.sp.toPx()
                    paint.color = color.toArgb()

                    canvas.nativeCanvas.drawText(
                        text,
                        outlineWidth.toPx(),
                        paint.fontSpacing + outlineWidth.toPx(),
                        paint
                    )
                }
            }
    )
}


