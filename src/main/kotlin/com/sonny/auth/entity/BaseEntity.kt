package com.sonny.auth.entity

import org.springframework.data.annotation.*
import java.time.LocalDateTime

abstract class BaseEntity {
    @Id
    var id: Long = 0

    @CreatedDate
    var createdAt: LocalDateTime? = LocalDateTime.now()

    @CreatedBy
    var createdBy: String? = null

    @LastModifiedDate
    var modifiedAt: LocalDateTime? = null

    @LastModifiedBy
    var modifiedBy: String? = null
}
