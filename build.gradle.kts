plugins {
    `java-library`
    id("net.kyori.indra") version "2.0.1"
    id("net.kyori.indra.checkstyle") version "2.0.1"
    id("net.kyori.indra.license-header") version "2.0.1"
    id("net.kyori.indra.publishing") version "2.0.1"
}

group = "me.machinemaker"
version = "0.1.1-SNAPSHOT"
description = "A config library"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("org.yaml:snakeyaml:1.27")

    // jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.0")

    // tests
    testImplementation("org.yaml:snakeyaml:1.27")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
