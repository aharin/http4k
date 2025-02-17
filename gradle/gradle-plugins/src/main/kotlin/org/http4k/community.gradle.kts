package org.http4k

plugins {
    id("org.http4k.internal.module")
    id("org.http4k.internal.publishing")
}

group = "org.http4k"

if (!project.name.contains("serverless")) {
    plugins {
        id("org.http4k.api-docs")
    }
}
