/*
 * Copyright 2024 NGApps Dev (https://github.com/ngapp-dev). All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ngapp.quottie.core.ui

import KottieAnimation
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import dev.icerock.moko.resources.compose.readTextAsState
import dev.icerock.moko.resources.compose.stringResource
import kottieComposition.KottieCompositionSpec
import kottieComposition.animateKottieCompositionAsState
import kottieComposition.rememberKottieComposition

@Composable
fun ErrorView(
    message: String,
    modifier: Modifier = Modifier,
    canRetry: Boolean = false,
    onClickRetry: () -> Unit = {},
) {
    var animation by rememberSaveable { mutableStateOf("") }
    val assetContent by SharedRes.files.error_json.readTextAsState()
    LaunchedEffect(Unit) {
        animation = assetContent ?: ""
    }

    val composition = rememberKottieComposition(KottieCompositionSpec.File(animation))
    val animationState by animateKottieCompositionAsState(composition = composition)

    AnimatedVisibility(
        visible = animationState.progress > 0f,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Column(
            modifier = modifier.onPlaced { _ -> },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KottieAnimation(
                composition = composition,
                progress = { animationState.progress },
                modifier = modifier
                    .widthIn(max = 300.dp, min = 100.dp)
                    .heightIn(max = 300.dp, min = 100.dp)
            )
            QuottieText(
                text = message,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = Color.Red,
                textAlign = TextAlign.Center
            )
            if (canRetry) {
                OutlinedButton(
                    onClick = onClickRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                ) {
                    QuottieText(text = stringResource(SharedRes.strings.button_try_again))
                }
            }
        }
    }
}
