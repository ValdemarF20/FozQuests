import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.2.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "net.summit"
version = "0.1.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "Aikar"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
}

dependencies {
    compileOnly(libs.paper) {
        exclude("org.yaml", "snakeyaml")
        exclude("com.google.guava")
        exclude("com.google.code.gson")
    }
    compileOnly(libs.annotations)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    compileOnly(libs.protocollib)

    implementation("com.google.guava:guava:33.0.0-jre") // Vulnerable dependency

    implementation(libs.hikari)
    implementation(libs.triumph) {
        // Triumph guis (Gui api)
        exclude("com.google.code.gson")
    }
    implementation(libs.gson)
    implementation(libs.aikar)// ACF (Command framework)

    implementation(libs.configuratecore)
    implementation(libs.configurateyaml)
    implementation(libs.configurategson)
}

val targetJavaVersion = 17
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}

tasks {
    withType<JavaCompile>().configureEach {
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }

        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName.set("${project.name}-${project.version}.jar")

        relocate("dev.triumphteam.guis", "cc.valdemar.foz.guis")
        relocate("co.airkar.commands", "cc.valdemar.foz.acf")
        relocate("co.airkar.locales", "cc.valdemar.foz.locales")
        relocate("org.spongepowered.configurate", "cc.valdemar.foz.configurate")
    }

    runServer {
        minecraftVersion("1.19.4")
    }

    clean {
        delete("run/plugins/FozQuests")
    }
}

bukkit {
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    main = "cc.valdemar.foz.fozquests.FozQuests"
    apiVersion = "1.19"
    authors = listOf("Valdemar")
    version = "${project.version}"

    depend = listOf("ProtocolLib")
}