plugins {
    `java-library`
}

group = "me.machinemaker.lectern"
version = "0.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":lectern-core"))
    implementation("org.yaml:snakeyaml:1.29")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}