plugins {
    id("java")
    kotlin("jvm") version "2.4.21"
    id("org.openapi.generator") version "7.27.0"
}

group = "gov.orion"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

val generatedSourcesDir = layout.buildDirectory.dir("generated/openapi")

openApiGenerate {
    generatorName.set("java")
    library.set("native")
    inputSpec.set(layout.projectDirectory.file("openapi.json").asFile.absolutePath)
    outputDir.set(generatedSourcesDir.get().asFile.absolutePath)
    apiPackage.set("gov.orion.api")
    modelPackage.set("gov.orion.model")
    invokerPackage.set("gov.orion.invoker")
    configOptions.set(
        mapOf(
            "useJakartaEe" to "true",
            "openApiNullable" to "false",
            "hideGenerationTimestamp" to "true"
        )
    )
}

sourceSets.main {
    java.srcDir(generatedSourcesDir.map { it.dir("src/main/java") })
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}

tasks.compileJava {
    dependsOn(tasks.openApiGenerate)
}

dependencies {
    implementation("com.microsoft.signalr:signalr:10.0.14")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.8")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.21.8")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.apache.httpcomponents:httpmime:4.5.14")
    // needed to satisfy Intellij's Checkmarx Vuln scanner
    constraints {
        implementation("commons-codec:commons-codec:1.23.0") {
            because("httpmime:4.5.14 pulls vulnerable commons-codec:1.11")
        }
        implementation("commons-logging:commons-logging:1.4.0") {
            because("httpmime:4.5.14 pulls older commons-logging:1.2")
        }
    }

    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
