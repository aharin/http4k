package org.http4k.mcp.client.internal

import org.http4k.mcp.client.McpResult
import org.http4k.mcp.model.RequestId
import org.http4k.mcp.protocol.messages.ClientMessage
import org.http4k.mcp.protocol.messages.McpRpc
import java.time.Duration

internal fun interface McpRpcSender {
    operator fun invoke(
        rpc: McpRpc,
        message: ClientMessage,
        timeout: Duration,
        requestId: RequestId
    ): McpResult<RequestId>
}
