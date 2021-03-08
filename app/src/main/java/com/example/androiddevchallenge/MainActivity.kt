/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect.Companion.cornerPathEffect
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.extensions.format
import com.example.androiddevchallenge.model.Time
import com.example.androiddevchallenge.ui.theme.JetimeTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<TimeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetimeTheme {
                Surface {
                    TimerScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimerScreen(viewModel: TimeViewModel) {
    val timeLeft: Time? by viewModel.timeLeft.observeAsState()
    Column(
        modifier = Modifier
            .fillMaxHeight(1f)
            .fillMaxWidth(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h3,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(250.dp)) {
                CircularView(viewModel)
                TimeStartView(viewModel)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TimerView(timeLeft!!, viewModel)
    }
}

@Composable
fun CircularView(
    viewModel: TimeViewModel
) {
    val timeLeft: Time? by viewModel.timeLeft.observeAsState()

    val primaryColor = MaterialTheme.colors.primary
    val secondaryColor = MaterialTheme.colors.secondary

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(percent = 50)
            )
            .animateContentSize()
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            if (canvasWidth != canvasHeight)
                return@Canvas

            val ringSize = 48f
            val r = canvasWidth / 2

            drawCircle(
                color = secondaryColor.copy(0.7f),
                radius = canvasWidth / 2,
                style = Stroke(
                    width = ringSize
                )
            )

            var radius = (canvasWidth - 2f * ringSize) / 2f
            var totalPart = (2 * PI * radius) / 12f
            var offPart = totalPart - 8.0f
            drawCircle(
                color = primaryColor.copy(0.7f),
                radius = radius,
                style = Stroke(
                    width = ringSize,
                    pathEffect = dashPathEffect(floatArrayOf(8f, offPart.toFloat()), 0f)
                )
            )

            drawCircle(
                color = primaryColor.copy(0.3f),
                radius = (canvasWidth - ringSize) / 2,
                style = Stroke(
                    width = ringSize
                )
            )

            val timeTotal = viewModel.timeTotal
            if (timeTotal > 0) {
                val timeRemain = timeLeft!!.inMillis()
                val sweepAngle = 360F * (timeRemain.toFloat() / timeTotal.toFloat())
                val topLeft = Offset(ringSize / 4, ringSize / 4)
                drawArc(
                    color = primaryColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(size.width - topLeft.x * 2, size.height - topLeft.y * 2),
                    style = Stroke(
                        width = ringSize / 2,
                        cap = StrokeCap.Round
                    )
                )

                // line
                val a = (sweepAngle - 90) / 180 * PI
                radius = r - 2 * ringSize
                val x = center.x + radius * cos(a)
                val y = center.y + radius * sin(a)

                drawLine(
                    start = center,
                    end = Offset(x.toFloat(), y.toFloat()),
                    color = primaryColor,
                    strokeWidth = 16.0f,
                    cap = StrokeCap.Round,
                    pathEffect = cornerPathEffect(8f)
                )
            }
        }
    }
}

@Composable
fun TimeStartView(
    viewModel: TimeViewModel
) {
    Box(Modifier.size(250.dp)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (viewModel.timeTotal == 0L) {
                RoundButton {
                    IconButton(
                        onClick = {
                            viewModel.startTimer()
                        },
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_play_arrow_24),
                            contentDescription = stringResource(R.string.button_start),
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                    }
                }
            } else {
                RoundButton {
                    IconButton(
                        onClick = {
                            viewModel.stopTimer()
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_stop_24),
                            contentDescription = stringResource(R.string.button_stop),
                            tint = MaterialTheme.colors.secondaryVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimerView(
    timeLeft: Time,
    viewModel: TimeViewModel,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TimeButton(
            viewModel = viewModel,
            timeText = timeLeft.getHours().format(2),
            timeLabel = "HH"
        ) {
            viewModel.addHours(it)
        }

        Spacer(modifier = Modifier.width(16.dp))
        TimeButton(
            viewModel = viewModel,
            timeText = timeLeft.getMinutes().format(2),
            timeLabel = "MM"
        ) {
            viewModel.addMinutes(it)
        }

        Spacer(modifier = Modifier.width(16.dp))
        TimeButton(
            viewModel = viewModel,
            timeText = timeLeft.getSeconds().format(2),
            timeLabel = "SS"
        ) {
            viewModel.addSeconds(it)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TimeButton(
    viewModel: TimeViewModel,
    timeText: String,
    timeLabel: String,
    onValueChange: (value: Int) -> Unit,
) {
    Surface(
        color = MaterialTheme.colors.secondary.copy(0.1F),
        shape = RoundedCornerShape(percent = 50),
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.secondary.copy(alpha = 0.7F),
                shape = RoundedCornerShape(percent = 50),
            )
            .defaultMinSize(56.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconButton(
                onClick = {
                    if (viewModel.timeTotal == 0L)
                        onValueChange.invoke(1)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.button_up),
                )
            }

            Text(
                text = timeText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body1,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 2.dp)
            )

            Text(
                text = timeLabel,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            IconButton(
                onClick = {
                    if (viewModel.timeTotal == 0L)
                        onValueChange.invoke(-1)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = stringResource(R.string.button_down),
                )
            }
        }
    }
}

@Composable
fun RoundButton(
    content: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colors.primary,
        shape = RoundedCornerShape(percent = 50),
        content = content
    )
}