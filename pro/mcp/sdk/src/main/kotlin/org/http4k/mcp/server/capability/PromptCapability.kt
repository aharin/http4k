package org.http4k.mcp.server.capability

import org.http4k.core.Request
import org.http4k.jsonrpc.ErrorMessage.Companion.InternalError
import org.http4k.jsonrpc.ErrorMessage.Companion.InvalidParams
import org.http4k.lens.LensFailure
import org.http4k.mcp.PromptHandler
import org.http4k.mcp.PromptRequest
import org.http4k.mcp.PromptWithClientHandler
import org.http4k.mcp.model.Prompt
import org.http4k.mcp.protocol.McpException
import org.http4k.mcp.protocol.messages.McpPrompt
import org.http4k.mcp.server.protocol.Client
import org.http4k.mcp.server.protocol.Client.Companion

interface PromptCapability : ServerCapability, PromptWithClientHandler, PromptHandler {
    fun toPrompt(): McpPrompt

    fun get(mcp: McpPrompt.Get.Request, client: Client, http: Request): McpPrompt.Get.Response
}

fun PromptCapability(prompt: Prompt, handler: PromptHandler) =
    PromptCapability(prompt) { request, _ -> handler(request) }

fun PromptCapability(prompt: Prompt, handler: PromptWithClientHandler) = object : PromptCapability {
    override fun toPrompt() = McpPrompt(prompt.name, prompt.description, prompt.args.map {
        McpPrompt.Argument(it.meta.name, it.meta.description, it.meta.required)
    })

    override fun get(mcp: McpPrompt.Get.Request, client: Client, http: Request) = try {
        handler(PromptRequest(mcp.arguments, http), client)
            .let { McpPrompt.Get.Response(it.messages, it.description) }
    } catch (e: LensFailure) {
        throw McpException(InvalidParams, e)
    } catch (e: Exception) {
        throw McpException(InternalError, e)
    }

    override fun invoke(p1: PromptRequest, client: Client) = handler(p1, client)
    override fun invoke(p1: PromptRequest) = handler(p1, Companion.NoOp)
}
