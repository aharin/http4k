package org.http4k.routing

import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.routing.PredicateResult.Matched
import org.http4k.routing.PredicateResult.NotMatched

interface Predicate {
    val description: String
    operator fun invoke(request: Request): PredicateResult

    companion object {

        operator fun invoke(
            description: String = "",
            notMatchedStatus: Status = NOT_FOUND,
            predicate: (Request) -> Boolean
        ) = object : Predicate {
            override val description: String = description
            override fun invoke(request: Request): PredicateResult =
                if (predicate(request)) Matched else NotMatched(notMatchedStatus)

            override fun toString(): String = description
        }
    }
}

sealed class PredicateResult {
    data object Matched : PredicateResult()
    data class NotMatched(val status: Status = NOT_FOUND) : PredicateResult()
}

val All = Predicate("all") { true }
val orElse = All
fun Predicate.and(other: Predicate) = when (this) {
    All -> other
    else -> when (other) {
        All -> this
        else -> Predicate("($this AND $other)") { this(it) is Matched && other(it) is Matched }
    }
}

fun Predicate.or(other: Predicate) = when (this) {
    All -> other
    else -> when (other) {
        All -> this
        else -> Predicate("($this OR $other)") { this(it) is Matched || other(it) is Matched }
    }
}

fun Predicate.not(): Predicate = Predicate("NOT $this") { this(it) !is Matched }

fun ((Request) -> Boolean).asPredicate(name: String = "") = Predicate(name, NOT_FOUND, this)

