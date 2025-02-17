package org.http4k.core

import org.http4k.websocket.WsFilter
import org.http4k.websocket.WsResponse
import org.http4k.websocket.WsStatus
import org.http4k.websocket.then
import java.time.Duration
import java.time.Instant

data class WsTransaction(
    override val request: Request,
    override val response: WsResponse,
    val status: WsStatus,
    override val duration: Duration,
    override val labels: Map<String, String> = defaultLabels(request, response),
    override val start: Instant
) : ProtocolTransaction<WsResponse> {
    fun label(name: String, value: String) = copy(labels = labels + (name to value))
}

fun WsFilter.then(poly: PolyHandler): PolyHandler = poly.copy(ws = poly.ws?.let { then(it) })

