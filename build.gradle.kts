plugins {
    java
    application
}

group = "com.dsaviz"
version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    implementation("com.formdev:flatlaf:3.5.4")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
}

application {
    mainClass.set("com.dsaviz.App")
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}
