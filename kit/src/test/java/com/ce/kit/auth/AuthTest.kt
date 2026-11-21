package com.ce.kit.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Modifier

class AuthTest {
    @Test fun tokenFailureMustSignInAgainOnlyForRejectedSignIn() {
        assertTrue(TokenFailure.mustSignInAgain("INVALID_GRANT"))
        assertFalse(TokenFailure.mustSignInAgain(null))
        assertFalse(TokenFailure.mustSignInAgain("server_error"))
    }

    @Test fun noAuthorizationExceptionIsAConnectionProblem() {
        assertNotNull(TokenFailure.of(null))
    }

    @Test fun tokensAreAPlainDataClass() {
        val t = Tokens("r", "a", 5L)
        assertEquals(t, Tokens("r", "a", 5L))
        assertNull(t.copy(refreshToken = null).refreshToken)
    }

    @Test fun redirectSchemeIsTheClientIdReversedTheGoogleWay() {
        assertEquals("com.googleusercontent.apps.123-abc", GoogleAuth.redirectScheme("123-abc.apps.googleusercontent.com"))
    }

    @Test fun tokenStoreRequiresAliasAndPrefsWithoutDefaults() {
        // Kotlin default values would add a synthetic constructor with a bit mask; there must be none.
        val ctors = TokenStore::class.java.constructors
        assertEquals(1, ctors.size)
        assertEquals(3, ctors[0].parameterCount)
        assertTrue(Modifier.isPublic(ctors[0].modifiers))
    }
}
