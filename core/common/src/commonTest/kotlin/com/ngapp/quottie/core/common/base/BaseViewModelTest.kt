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

package com.ngapp.quottie.core.common.base

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private data class TestUiState(val counter: Int = 0) : BaseUiState

private sealed interface TestAction : BaseAction {
    data object Increment : TestAction
}

private sealed interface TestEvent : BaseEvent {
    data object Incremented : TestEvent
}

private class TestViewModel : BaseViewModel<TestUiState, TestAction, TestEvent>() {

    override fun provideUiState(): TestUiState = TestUiState()

    override fun triggerAction(action: TestAction) {
        when (action) {
            TestAction.Increment -> safeLaunch {
                _uiState.update { it.copy(counter = it.counter + 1) }
                eventChannel.send(TestEvent.Incremented)
            }
        }
    }
}

class BaseViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun uiState_startsAtProvidedInitialValue() {
        val viewModel = TestViewModel()

        assertEquals(TestUiState(counter = 0), viewModel.uiState.value)
    }

    @Test
    fun triggerAction_updatesUiStateAndEmitsEvent() = runTest(testDispatcher) {
        val viewModel = TestViewModel()
        val receivedEvents = mutableListOf<TestEvent>()
        val collector = launch { viewModel.events.collect { receivedEvents.add(it as TestEvent) } }

        viewModel.triggerAction(TestAction.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.counter)
        assertEquals(1, receivedEvents.size)
        assertEquals(TestEvent.Incremented, receivedEvents.first())

        collector.cancel()
    }

    @Test
    fun triggerAction_calledTwice_accumulatesState() = runTest(testDispatcher) {
        val viewModel = TestViewModel()

        viewModel.triggerAction(TestAction.Increment)
        viewModel.triggerAction(TestAction.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.uiState.value.counter)
    }
}
