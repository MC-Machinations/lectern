group = "me.machinemaker.lectern"
description = "YAML extension of the core library"

dependencies {
    api(project(":lectern-core"))
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.2")
}
