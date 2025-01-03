package org.http4k.connect.amazon

import org.http4k.config.Environment
import org.http4k.connect.amazon.core.model.AwsProfile
import org.http4k.connect.amazon.core.model.ProfileName
import java.nio.file.Path

fun RegionProvider.Companion.Profile(profileName: ProfileName, credentialsPath: Path, configPath: Path = credentialsPath.resolveSibling("config")) =
    RegionProvider { AwsProfile.loadProfiles(credentialsPath, configPath)[profileName]?.region }

fun RegionProvider.Companion.Profile(env: Environment) =
    Profile(AWS_PROFILE(env), AWS_CREDENTIAL_PROFILES_FILE(env), AWS_CONFIG_FILE(env))
