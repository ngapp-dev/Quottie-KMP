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

package com.ngapp.quottie.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.ngapp.quottie.core.database.model.quote.QuoteOfTheDayEntity

@Dao
interface QuoteOfTheDayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuoteOfTheDay(quote: QuoteOfTheDayEntity)

    @Query("DELETE FROM quote_of_the_day")
    suspend fun deleteQuoteOfTheDay()

    @Query("SELECT * FROM quote_of_the_day LIMIT 1")
    suspend fun getQuoteOfTheDay(): QuoteOfTheDayEntity?

    @Transaction
    suspend fun replaceQuoteOfTheDay(newQuote: QuoteOfTheDayEntity) {
        deleteQuoteOfTheDay()
        insertQuoteOfTheDay(newQuote)
    }
}