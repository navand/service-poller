import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import com.github.gradle.node.npm.task.*

plugins {
  java
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
  id("com.github.node-gradle.node") version "3.1.1"
}

group = "com.kry"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.2.1"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "com.kry.ServicePoller.MainVerticle"
val launcherClassName = "com.kry.ServicePoller.Main"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-web-validation")
  implementation("io.vertx:vertx-mysql-client")
  implementation("io.vertx:vertx-sql-client-templates:4.2.1")
  implementation("io.vertx:vertx-auth-jdbc")
  implementation("io.vertx:vertx-auth-jwt")
  implementation("io.vertx:vertx-rx-java3")
  implementation("io.vertx:vertx-health-check:4.2.1")
  implementation("io.vertx:vertx-micrometer-metrics:4.2.1")
  implementation("io.micrometer:micrometer-registry-prometheus:latest.release")
  implementation("com.fasterxml.jackson.core:jackson-core:2.10.1")
  implementation("com.fasterxml.jackson.core:jackson-annotations:2.10.1")
  implementation("com.fasterxml.jackson.core:jackson-databind:2.10.1")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.10.1")
  implementation("org.flywaydb:flyway-core:7.15.0")
  implementation("mysql:mysql-connector-java:5.1.6")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
  testImplementation("org.testcontainers:mysql:1.16.2")
}

node {
  version.set("14.13.0")
  npmVersion.set("7.16.0")
  yarnVersion.set("")
  npmInstallCommand.set("install")
  distBaseUrl.set("https://nodejs.org/dist")
  download.set(true)
  workDir.set(file("${project.projectDir}/.cache/nodejs"))
  npmWorkDir.set(file("${project.projectDir}/.cache/npm"))
  yarnWorkDir.set(file("${project.projectDir}/.cache/yarn"))
  nodeProjectDir.set(file("${project.projectDir}/src/main/frontend"))
}

val buildFrontend by tasks.creating(NpmTask::class) {
  npmCommand.set(listOf("run", "build"))
  dependsOn("npmInstall")
}

val copyToWebRoot by tasks.creating(Copy::class) {
  from("src/main/frontend/build")
  destinationDir = File("${buildDir}/classes/java/main/webroot")
  dependsOn(buildFrontend)
}

val processResources by tasks.getting(ProcessResources::class) {
  dependsOn(copyToWebRoot)
}

java {
  sourceCompatibility = JavaVersion.VERSION_11
  targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
