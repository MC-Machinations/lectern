plugins {
    `java-library`
    id("net.kyori.indra")
    id("net.kyori.indra.checkstyle")
    id("net.kyori.indra.license-header")
    id("net.kyori.indra.publishing")
}

group = "me.machinemaker.lectern"
version = "0.2-SNAPSHOT"
description = "Core for a configuration library"

repositories {
    mavenCentral()
}

dependencies {
    // annotations
    compileOnly("org.jetbrains:annotations:22.0.0")

    implementation("io.leangen.geantyref:geantyref:1.3.4")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

indra {
    javaVersions {
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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}