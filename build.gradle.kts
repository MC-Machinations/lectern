plugins {
    `java-library`
    id("net.kyori.indra") version "2.0.6"
    id("net.kyori.indra.checkstyle") version "2.0.6"
    id("net.kyori.indra.license-header") version "2.0.6"
    id("net.kyori.indra.publishing") version "2.0.6"
}

group = "me.machinemaker"
version = "0.1.1-SNAPSHOT"
description = "A config library"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:22.0.0")
    compileOnly("org.yaml:snakeyaml:1.27")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.4")

    // tests
    testImplementation("commons-io:commons-io:2.11.0")
    testImplementation("org.yaml:snakeyaml:1.27")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

indra {
    javaVersions {
        testWith(16)
        target(16)
    }

    github("MC-Machinations", "lectern") {
        ci(true)
    }

    gpl3OnlyLicense()

    publishReleasesTo("central", "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    publishSnapshotsTo("snapshots", "https://oss.sonatype.org/content/repositories/snapshots")

    configurePublications {
        pom {
            developers {
                developer {
                    id.set("Machine_Maker")
                    email.set("machine@machinemaker.me")
                }
            }
        }
    }
}

license {
    header(rootProject.file("LICENSE_HEADER"))
}

tasks {
    javadoc {
        source = sourceSets.main.get().allJava

        exclude("me/machinemaker/lectern/yaml")
    }

    compileTestJava {
        options.compilerArgs.add("-parameters")
    }

    checkstyleTest {
        enabled = false
    }
}
