package com.sonny.auth.model

import com.fasterxml.jackson.annotation.JsonProperty

data class AuthToken(
    @JsonProperty("access_token")
    var accessToken: String,

    @JsonProperty("expires_in")
    var expiresIn: Long,

    @JsonProperty("refresh_token")
    var refreshToken: String,

    @JsonProperty("refresh_expires_in")
    var refreshExpiresIn: Long,

    @JsonProperty("token_type")
    var tokenType: String
)
