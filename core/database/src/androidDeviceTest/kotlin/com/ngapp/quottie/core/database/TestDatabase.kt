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

package com.ngapp.quottie.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider

/**
 * Builds an in-memory [AppDatabase] backed by the real Android SQLite framework, running on a
 * device/emulator via the instrumentation [Context]. Content is lost once the test process dies.
 */
internal fun createTestDatabase(): AppDatabase {
    val context: Context = ApplicationProvider.getApplicationContext()
    return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
}
