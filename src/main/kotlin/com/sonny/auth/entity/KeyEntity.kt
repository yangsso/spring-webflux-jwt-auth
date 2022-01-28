package com.sonny.auth.entity

import org.springframework.data.relational.core.mapping.Table

@Table("json_web_key")
class KeyEntity(
    val kid: String,
    val service: String,
    val type: String,
    val use_type:  String,
    val alg: String,
    val publicKey: String,
    val privateKey: String
) : BaseEntity()
