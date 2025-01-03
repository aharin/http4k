package org.http4k.testing

import org.http4k.testing.CompileProject.Companion.Result.Failed
import org.http4k.testing.CompileProject.Companion.Result.Ok
import java.io.File
import java.io.InputStream

fun interface CompileProject {

    operator fun invoke(): Result

    companion object {
        sealed interface Result {
            data object Ok : Result
            data class Failed(val errorStream: InputStream) : Result
        }

        fun Gradle(task: String = "compileKotlin") = CompileProject {
            val process = ProcessBuilder().command("./gradlew", task)
                .directory(File(".").absoluteFile)
                .start()

            when (process.waitFor()) {
                0 -> Ok
                else -> Failed(process.errorStream)
            }
        }
    }
}
