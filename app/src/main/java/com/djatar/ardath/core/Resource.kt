/*
 * Copyright (©) 2024 Dikidjatar
 * All Rights Reserved.
 */

package com.djatar.ardath.core

sealed class Resource<T>(
    var data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}