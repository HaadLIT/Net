plugins {
    id("java")
    application
}

group = "com.haadlit_sp"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.haadlit_sp.Main")
}

repositories {
    mavenCentral()

}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}


tasks.test {
    useJUnitPlatform()
}

// Make `gradlew jar` produce a directly runnable jar. The app has no
// third-party dependencies, so the plain jar is fully self-contained.
tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.haadlit_sp.Main"
    }
}
