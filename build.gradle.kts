plugins {
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "ru.nilsson03"
version = "1.0-BETA"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")

    compileOnly(files("libs/baselibrary-1.2.8-RELEASE.jar"))
    compileOnly(files("libs/InvUI-v1.49.jar"))
    compileOnly(files("libs/database-core-1.0-RELEASE.jar"))

    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.44.0")

    implementation("mysql:mysql-connector-java:8.0.33")

    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.6")
    implementation(kotlin("stdlib"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

kotlin {
    jvmToolchain(16)
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    jar {
        archiveBaseName.set("nvTreasures")
        doLast {
            val destDir = file("D:/Minecraft Develop/Test plugins/plugins")
            if (!destDir.exists()) {
                destDir.mkdirs()
            }
            copy {
                from(archiveFile.get().asFile)
                into(destDir)
            }
        }
    }
}