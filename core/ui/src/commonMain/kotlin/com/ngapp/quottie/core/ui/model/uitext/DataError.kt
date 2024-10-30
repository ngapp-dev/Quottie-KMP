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

package com.ngapp.quottie.core.ui.model.uitext

import com.ngapp.quottie.SharedRes
import com.ngapp.quottie.core.common.result.DataError
import dev.icerock.moko.resources.StringResource

fun DataError.asStringResource(): StringResource {
    return when (this) {
        DataError.Network.BAD_REQUEST -> SharedRes.strings.error_bad_request
        DataError.Network.AUTHORIZATION_ERROR -> SharedRes.strings.error_verification_code
        DataError.Network.NOT_FOUND -> SharedRes.strings.error_no_such_email
        DataError.Network.REQUEST_TIMEOUT -> SharedRes.strings.error_the_request_timed_out
        DataError.Network.TOO_MANY_REQUESTS -> SharedRes.strings.error_youve_hit_your_rate_limit
        DataError.Network.NO_INTERNET -> SharedRes.strings.error_no_internet
        DataError.Network.PAYLOAD_TOO_LARGE -> SharedRes.strings.error_file_too_large
        DataError.Network.SERVER_ERROR -> SharedRes.strings.error_server_error
        DataError.Network.SERIALIZATION -> SharedRes.strings.error_unknown_error
        DataError.Network.CONFLICT -> SharedRes.strings.error_no_such_email
        DataError.Network.UNKNOWN -> SharedRes.strings.error_unknown_error
        DataError.Network.FORBIDDEN -> SharedRes.strings.error_forbidden
        DataError.Local.DISK_FULL -> SharedRes.strings.error_disk_full
    }
}