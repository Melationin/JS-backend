plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.6"
}

base {
    archivesName.set("js-backend-graaljs")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":api"))
    implementation("org.graalvm.polyglot:polyglot:24.0.1")
    implementation("org.graalvm.polyglot:js:24.0.1")
}

tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    mergeServiceFiles()
}

tasks.named("build") {
    dependsOn(tasks.named("shadowJar"))
}
