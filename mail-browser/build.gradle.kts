plugins {
    id("java-library")
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("io.freefair.lombok") version "8.0.1"
    id("org.javamodularity.moduleplugin") version "1.8.12" apply false
    id("org.beryx.jlink") version "2.26.0"
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

jlink {
    addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "mail-browser"
    }
}

application {
    mainModule.set("mail.browser.main")
    mainClass.set("fr.leswebdevs.MainApp")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.26")

    api("jakarta.mail:jakarta.mail-api:2.1.1")

    implementation("org.eclipse.angus:angus-mail:2.0.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}