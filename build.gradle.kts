plugins {
    `maven-publish`
    signing
    id("net.kyori.indra") version "2.0.6"
    id("net.kyori.indra.checkstyle") version "2.0.6"
    id("net.kyori.indra.license-header") version "2.0.6"
}

group = "me.machinemaker"
version = "0.3.0"
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
    apply(plugin="maven-publish")
    apply(plugin="signing")

    repositories {
        mavenCentral()
    }

    dependencies {
        compileOnly("org.jetbrains:annotations:24.0.1")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
    }

    indra {
        javaVersions {
            target(17)
            testWith(17)
        }

        github("MC-Machinations", "lectern") {
            ci(true)
        }

        gpl3OnlyLicense()
    }

    fun version(): String {
        return rootProject.version.toString()
    }

    val isSnapshot = version().endsWith("-SNAPSHOT")

    fun MavenPublication.standardConfig(versionName: String) {
        group = project.group
        artifactId = project.name
        version = versionName

        from(components["java"])

        withoutBuildIdentifier()
        pom {
            val repoPath = "MC-Machinations/lectern"
            val repoUrl = "https://github.com/$repoPath"

            name.set(project.name)
            url.set(repoUrl)
            description.set(rootProject.description)
            inceptionYear.set("2022")
            packaging = "jar"

            licenses {
                license {
                    name.set("GNU General Public License Version 3.0")
                    url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                    distribution.set("repo")
                }
            }

            issueManagement {
                system.set("GitHub")
                url.set("$repoUrl/issues")
            }

            developers {
                developer {
                    id.set("Machine_Maker")
                    name.set("Jake Potrebic")
                    email.set("machine@machinemaker.me")
                    url.set("https://github.com/Machine-Maker")
                }
            }

            scm {
                url.set(repoUrl)
                connection.set("scm:git:$repoUrl.git")
                developerConnection.set("scm:git:git@github.com:$repoPath.git")
            }
        }

    }

    publishing {

        publications {
            register<MavenPublication>("maven") {
                standardConfig(version())
            }
        }

        repositories {
            val url = if (isSnapshot) {
                "https://oss.sonatype.org/content/repositories/snapshots"
            } else {
                "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            }
            maven(url) {
                name = "ossrh"
                credentials(PasswordCredentials::class)
            }
        }
    }

    signing {
        useGpgCmd()
        sign(publishing.publications["maven"])
    }

    tasks {
        withType<PublishToMavenRepository> {
            dependsOn(check)
        }

        test {
            useJUnitPlatform()
        }
    }
}
