plugins {
    id("schat.api")
    id("schat.shadow")
}

description = "Bungeecord implementation of the sChat platform."

dependencies {
    api(project(":schat-platform"))

    implementation(libs.bungeecord)
    implementation(libs.adventure.platform.bungeecord)

    testImplementation(libs.mockbukkit)
    testImplementation(testFixtures(project(":schat-platform")))

    testFixturesImplementation(testFixtures(project(":schat-platform")))
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    dependencies {
        include(dependency("net.kyori::"))
    }

    val lib = "net.silthus.schat.lib"
    relocate("net.kyori", "$lib.kyori")
}
