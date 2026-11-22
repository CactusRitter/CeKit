package com.ce.kit.auth

/**
 * Gives an API client a valid access token for one account of the [store]: it loads the saved tokens, lets the
 * provider's own [refresh] (Google's or Microsoft's `freshAccessToken`; null = the user must sign in again) renew them
 * when needed and saves what comes back. Each app decides under which key it keeps an account's tokens.
 */
class TokenProvider(private val store: TokenStore) {
    /**
     * @param forceRefresh the server just refused the token: it is thrown away so that a new one is asked for.
     */
    suspend fun accessToken(key: String, forceRefresh: Boolean = false, refresh: suspend (Tokens) -> Tokens?): String? {
        val current = store.load(key) ?: return null
        val base = if (forceRefresh) current.copy(accessToken = null) else current
        val fresh = refresh(base) ?: return null
        if (fresh !== current) store.save(key, fresh)
        return fresh.accessToken
    }
}
