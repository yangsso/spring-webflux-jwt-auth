package com.sonny.auth.model

import com.sonny.auth.enum.KeyType
import com.sonny.auth.enum.UseType

data class KeyModel (
    val service: String,
    val type: KeyType,
    val use: UseType,
)
