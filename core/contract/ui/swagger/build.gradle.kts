import org.http4k.internal.ModuleLicense.Http4kCommunity

description = "DEPRECATED: use http4k-api-ui-swagger instead"

val license by project.extra { Http4kCommunity }

plugins {
    id("org.http4k.community")
}

dependencies {
    api(project(":http4k-api-openapi"))
    api("org.webjars:swagger-ui:_")

    testImplementation(testFixtures(project(":http4k-core")))
}
