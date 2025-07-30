package com.aether.myaether.bluesky

import work.socialhub.kbsky.auth.AuthProvider
import work.socialhub.khttpclient.HttpRequest
import work.socialhub.khttpclient.HttpResponse

class NoDPoPAuthProvider(
    val accessTokenJwt: String?,
    val refreshTokenJwt: String?
) : AuthProvider {

    override val did: String
        get() = "" // For refresh, DID might not be strictly necessary in the AuthProvider itself

    override val pdsDid: String
        get() = "" // Similar to did

    override fun beforeRequestHook(method: String, request: HttpRequest) {
        // Do nothing to prevent DPoP header generation
        // The refresh token is sent in the request body, not via Authorization header
    }

    override fun afterRequestHook(method: String, request: HttpRequest, response: HttpResponse): Boolean {
        // Do nothing or handle specific retry logic if needed, but for refresh, usually not
        return false
    }

    override var acceptLabelers: List<String> = emptyList()
}
