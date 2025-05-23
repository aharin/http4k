package org.http4k.mcp.server.protocol

import org.http4k.core.Request
import org.http4k.mcp.Client
import org.http4k.mcp.protocol.messages.McpPrompt

/**
 * Handles protocol traffic for prompts features.
 */
interface Prompts {
    fun get(req: McpPrompt.Get.Request, client: Client, http: Request): McpPrompt.Get.Response
    fun list(mcp: McpPrompt.List.Request, client: Client, http: Request): McpPrompt.List.Response
}
