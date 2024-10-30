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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.ngapp.quottie.core.desingsystem.component.QuottieIconButton
import com.ngapp.quottie.core.desingsystem.component.QuottieIconToggleButton
import com.ngapp.quottie.core.desingsystem.component.QuottieTag
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import com.ngapp.quottie.core.desingsystem.icon.QuottieIcons
import com.ngapp.quottie.core.model.quote.QuoteResource
import dev.icerock.moko.resources.compose.painterResource
import kotlin.text.uppercase

@Composable
fun QuoteResourceCard(
    quote: QuoteResource,
    isBookmarked: Boolean,
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    borderStroke: BorderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
    onToggleBookmark: (Boolean) -> Unit,
    onShareClick: () -> Unit,
    onTagClick: (String) -> Unit,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        colors = colors,
        border = borderStroke,
    ) {
        Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SelectionContainer {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            QuottieText(
                                text = "“${quote.content}”",
                                style = MaterialTheme.typography.bodyLarge,
                                fontStyle = FontStyle.Italic,
                            )
                            QuottieText(
                                text = "- ${quote.author}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    BookmarkButton(isBookmarked, onToggleBookmark)
                    ShareButton(onShareClick)
                }
            }
            QuoteTags(
                tags = quote.tags,
                onTagClick = onTagClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun BookmarkButton(
    isBookmarked: Boolean,
    onToggleBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    QuottieIconToggleButton(
        checked = isBookmarked,
        onCheckedChange = onToggleBookmark,
        modifier = modifier,
        icon = {
            Icon(
                painter = painterResource(QuottieIcons.BookmarkBorder),
                contentDescription = "",
            )
        },
        checkedIcon = {
            Icon(
                painter = painterResource(QuottieIcons.Bookmark),
                contentDescription = "",
            )
        },
    )
}

@Composable
private fun ShareButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    QuottieIconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(QuottieIcons.Share),
            contentDescription = "",
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuoteTags(
    tags: List<String>,
    onTagClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        tags.forEach { tag ->
            QuottieTag(
                onClick = { onTagClick(tag) },
                text = {
                    QuottieText(
                        text = tag.uppercase(),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
            )
        }
    }
}