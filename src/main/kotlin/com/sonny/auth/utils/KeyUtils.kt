package com.sonny.auth.utils

import java.nio.charset.StandardCharsets
import java.security.Key
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

fun Key.encodeBase64(): String{
    return String(Base64.getEncoder().encode(this.encoded), StandardCharsets.UTF_8)
}

fun String.decodeBase64(): ByteArray {
    return Base64.getDecoder().decode(this)
}

fun ByteArray.toPrivateKey(): PrivateKey {
    return KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(this))
}

fun ByteArray.toPublicKey(): PublicKey {
    return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(this)) as RSAPublicKey
}
