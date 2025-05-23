package org.http4k.security

import org.http4k.core.Filter
import org.http4k.core.NoOp
import org.http4k.core.Uri
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestLens

sealed class OAuthSecurity(
    override val filter: Filter,
    val name: String,
    val scopes: List<OAuthScope>,
    val refreshUrl: Uri?,
    val extraFields: Map<String, String> = emptyMap()
) : Security {
    companion object
}

class AuthCodeOAuthSecurity(
    val authorizationUrl: Uri,
    val tokenUrl: Uri,
    scopes: List<OAuthScope> = emptyList(),
    filter: Filter,
    name: String = "oauthSecurityAuthCode",
    refreshUrl: Uri? = null,
    extraFields: Map<String, String> = emptyMap()
) : OAuthSecurity(filter, name, scopes, refreshUrl, extraFields) {
    companion object
}

class ImplicitOAuthSecurity(
    val authorizationUrl: Uri,
    scopes: List<OAuthScope> = emptyList(),
    filter: Filter,
    name: String = "oauthSecurityImplicit",
    refreshUrl: Uri? = null,
    extraFields: Map<String, String> = emptyMap()
) : OAuthSecurity(filter, name, scopes, refreshUrl, extraFields) {

    companion object
}

class UserCredentialsOAuthSecurity(
    val tokenUrl: Uri,
    scopes: List<OAuthScope> = emptyList(),
    filter: Filter = Filter.NoOp,
    name: String = "oauthSecurityUserCredentials",
    refreshUrl: Uri? = null,
    extraFields: Map<String, String> = emptyMap()
) : OAuthSecurity(filter, name, scopes, refreshUrl, extraFields) {
    companion object {
        operator fun <T> invoke(
            tokenUrl: Uri,
            key: RequestLens<T>,
            lookup: (String) -> T?,
            refreshUrl: Uri? = null,
            scopes: List<OAuthScope> = emptyList()
        ) = UserCredentialsOAuthSecurity(
            tokenUrl = tokenUrl,
            scopes = scopes,
            filter = ServerFilters.BearerAuth(key, lookup),
            refreshUrl = refreshUrl,
        )
    }
}

class ClientCredentialsOAuthSecurity(
    val tokenUrl: Uri,
    scopes: List<OAuthScope> = emptyList(),
    filter: Filter = Filter.NoOp,
    name: String = "oauthSecurityClientCredentials",
    refreshUrl: Uri? = null,
    extraFields: Map<String, String> = emptyMap()
) : OAuthSecurity(filter, name, scopes, refreshUrl, extraFields) {
    companion object {
        operator fun <T> invoke(
            tokenUrl: Uri,
            key: RequestLens<T>,
            lookup: (String) -> T?,
            refreshUrl: Uri? = null,
            scopes: List<OAuthScope> = emptyList()
        ) = ClientCredentialsOAuthSecurity(
            tokenUrl = tokenUrl,
            scopes = scopes,
            filter = ServerFilters.BearerAuth(key, lookup),
            refreshUrl = refreshUrl,
        )
    }
}

data class OAuthScope(val name: String, val description: String = name)
