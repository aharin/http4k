package org.http4k.mcp.client.internal

import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import org.http4k.mcp.ResourceRequest
import org.http4k.mcp.ResourceResponse
import org.http4k.mcp.client.McpClient
import org.http4k.mcp.model.RequestId
import org.http4k.mcp.protocol.messages.McpResource
import org.http4k.mcp.protocol.messages.McpRpc
import org.http4k.mcp.util.McpNodeType

internal class ClientResources(
    private val queueFor: (RequestId) -> Iterable<McpNodeType>,
    private val tidyUp: (RequestId) -> Unit,
    private val sender: McpRpcSender,
    private val register: (McpRpc, NotificationCallback<*>) -> Any
) : McpClient.Resources {
    override fun onChange(fn: () -> Unit) {
        register(McpResource.List, NotificationCallback(McpResource.List.Changed.Notification::class) { fn() })
    }

    override fun list() = sender(McpResource.List, McpResource.List.Request()) { true }
        .map { reqId -> queueFor(reqId).also { tidyUp(reqId) } }
        .flatMap { it.first().asOrFailure<McpResource.List.Response>() }
        .map { it.resources }

    override fun read(request: ResourceRequest) =
        sender(McpResource.Read, McpResource.Read.Request(request.uri)) { true }
            .map { reqId -> queueFor(reqId).also { tidyUp(reqId) } }
            .flatMap { it.first().asOrFailure<McpResource.Read.Response>() }
            .map { ResourceResponse(it.contents) }
}
