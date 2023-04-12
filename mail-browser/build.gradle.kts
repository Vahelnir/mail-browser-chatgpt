plugins {
    id("java")
}

group = "fr.leswebdevs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("jakarta.mail:jakarta.mail-api:2.1.1")
    implementation("org.eclipse.angus:angus-mail:2.0.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}