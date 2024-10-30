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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemKey
import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.desingsystem.component.QuottieText
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun <T : Any> PagingGrid(
    modifier: Modifier = Modifier,
    state: LazyGridState = rememberLazyGridState(),
    lazyData: LazyPagingItems<T>,
    staticContent: (LazyGridScope.() -> Unit) = {},
    lazyContent: @Composable (T) -> Unit,
) {
    LazyVerticalGrid(
        state = state,
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(300.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        staticContent()
        items(count = lazyData.itemCount, key = lazyData.itemKey()) { index ->
            val item = lazyData[index]
            item?.let { lazyContent(it) }
        }
        lazyData.loadState.apply {
            when {
                refresh is LoadStateNotLoading && lazyData.itemCount < 1 -> {
                    item(key = "refresh_not_loading", span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            QuottieText(
                                text = stringResource(SharedRes.strings.no_items),
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                refresh is LoadStateLoading -> {
                    item(key = "refresh_loading", span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                append is LoadStateLoading -> {
                    item(key = "append_loading", span = { GridItemSpan(maxLineSpan) }) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(20.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }

                refresh is LoadStateError -> {
                    item(key = "refresh_error", span = { GridItemSpan(maxLineSpan) }) {
                        ErrorView(
                            message = stringResource(SharedRes.strings.error_server_error),
                            canRetry = true,
                            onClickRetry = { lazyData.retry() },
                            modifier = Modifier.fillMaxWidth(1f)
                        )
                    }
                }

                append is LoadStateError -> {
                    item(key = "append_error", span = { GridItemSpan(maxLineSpan) }) {
                        ErrorItem(
                            message = stringResource(SharedRes.strings.error_server_error),
                            onClickRetry = { lazyData.retry() },
                        )
                    }
                }
            }
        }
        item("safeDrawing") {
            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.safeDrawing))
        }
    }
}

@Composable
private fun ErrorItem(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        QuottieText(
            text = message,
            maxLines = 1,
            modifier = Modifier.weight(1f),
            color = Color.Red
        )
        OutlinedButton(onClick = onClickRetry) {
            QuottieText(text = stringResource(SharedRes.strings.button_try_again))
        }
    }
}