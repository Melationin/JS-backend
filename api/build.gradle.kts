plugins {
    `java-library`
    `maven-publish`
}

base {
    archivesName.set("js-backend-api")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "js-backend-api"
            from(components["java"])
        }
    }
}
