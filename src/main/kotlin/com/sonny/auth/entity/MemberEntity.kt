package com.sonny.auth.entity

import org.springframework.data.relational.core.mapping.Table

@Table("member")
class MemberEntity(
    val loginId: String,
    val password: String,
    val role: String
) : BaseEntity()
