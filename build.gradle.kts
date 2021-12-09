plugins {
    id("net.kyori.indra") version "2.0.6"
    id("net.kyori.indra.checkstyle") version "2.0.6"
    id("net.kyori.indra.license-header") version "2.0.6"
    id("net.kyori.indra.publishing") version "2.0.6" apply false
}

group = "me.machinemaker"
version = "0.2.1"
description = "A config library"

allprojects {
    apply(plugin="net.kyori.indra")
    apply(plugin="net.kyori.indra.license-header")

    license {
        header(rootProject.file("HEADER"))
    }

    indra {
        javaVersions {
            target(16)
            testWith(16)
        }
    }

    tasks {
        build {
            dependsOn(checkLicenses)
        }
    }
}

subprojects {
    apply(plugin="net.kyori.indra.publishing")

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:22.0.0")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    }

    indra {
        javaVersions {
            target(16)
            testWith(16)
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

    tasks {
        test {
            useJUnitPlatform()
        }
    }
}
