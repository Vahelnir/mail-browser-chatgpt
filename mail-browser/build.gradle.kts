plugins {
    id("java")
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("io.freefair.lombok") version "8.0.1"
    id("org.javamodularity.moduleplugin") version "1.8.12" apply false

}

plugins.withType<JavaPlugin>().configureEach {
    configure<JavaPluginExtension> {
        modularity.inferModulePath.set(true)
    }
}

javafx {
    version = "20"
    modules = listOf("javafx.controls", "javafx.fxml")
}

group = "fr.leswebdevs"
version = "1.0-SNAPSHOT"

application {
    mainModule.set("mail.browser.main")
    mainClass.set("fr.leswebdevs.MainApp")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.26")
    implementation("jakarta.mail:jakarta.mail-api:2.1.1")
    implementation("org.eclipse.angus:angus-mail:2.0.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}