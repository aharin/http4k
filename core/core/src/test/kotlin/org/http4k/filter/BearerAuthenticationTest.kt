package org.http4k.filter

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.http4k.core.Credentials
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.then
import org.http4k.filter.ServerFilters.BearerAuth
import org.http4k.lens.RequestKey
import org.http4k.security.CredentialsProvider
import org.junit.jupiter.api.Test

class BearerAuthenticationTest {

    @Test
    fun wrong_token_type() {
        val handler = BearerAuth("Basic dXNlcjpwYXNzd29yZA==").then { Response(OK) }
        val response = ClientFilters.BasicAuth("user", "password")
            .then(handler)(Request(GET, "/"))
        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun fails_to_authenticate() {
        val handler = BearerAuth("token").then { Response(OK) }
        val response = handler(Request(GET, "/"))
        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun authenticate_using_client_extension() {
        val handler = BearerAuth("token").then { Response(OK) }
        val response = ClientFilters.BearerAuth(CredentialsProvider { "token" }).then(handler)(Request(GET, "/"))
        assertThat(response.status, equalTo(OK))
    }

    @Test
    fun fails_to_authentic_with_non_bearer_token() {
        val handler = BearerAuth("Basic YmFkZ2VyOm1vbmtleQ==").then { Response(OK) }
        val response = ClientFilters.BasicAuth("badger", "monkey").then(handler)(Request(GET, "/"))
        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun fails_to_authenticate_if_credentials_do_not_match() {
        val handler = BearerAuth("token").then { Response(OK) }
        val response = ClientFilters.BearerAuth(CredentialsProvider { "not token" }).then(handler)(Request(GET, "/"))
        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun when_no_credentials_return_unauthorised() {
        val handler = BearerAuth("token").then { Response(OK) }
        val response = ClientFilters.BearerAuth(CredentialsProvider { null }).then(handler)(Request(GET, "/"))
        assertThat(response.status, equalTo(UNAUTHORIZED))
    }

    @Test
    fun populates_request_context_for_later_retrieval() {
        val key = RequestKey.required<Credentials>("credentials")

        val handler = BearerAuth(key) { Credentials(it, it) }
            .then { req -> Response(OK).body(key(req).toString()) }

        val response = ClientFilters.BearerAuth("token").then(handler)(Request(GET, "/"))

        assertThat(response.bodyString(), equalTo("Credentials(user=token, password=token)"))
    }
}
