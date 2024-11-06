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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun AuthorCardImage(
    imageUrl: String?,
    contentDescription: String,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier
) {
    var isLoadingOrError by remember { mutableStateOf(false) }
    val imageModifier = modifier
        .width(140.dp)
        .heightIn(max = 90.dp)
        .clip(MaterialTheme.shapes.medium)

    Box(modifier = imageModifier) {
        AsyncImage(
            model = imageUrl,
            contentScale = contentScale,
            alignment = Alignment.TopCenter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            onLoading = { isLoadingOrError = true },
            onSuccess = { isLoadingOrError = false },
            onError = { isLoadingOrError = true },
        )
        if (isLoadingOrError) {
            Image(
                painter = painterResource(QuottieIcons.LogoMono),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}