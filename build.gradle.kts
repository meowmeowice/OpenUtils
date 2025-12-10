import dev.architectury.pack200.java.Pack200Adapter
import net.fabricmc.loom.task.RemapJarTask
import org.apache.commons.lang3.SystemUtils

// Buildscript based on https://github.com/lineargraph/Forge1.8.9Template (Unlicense)
plugins {
    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("com.diffplug.spotless") version "8.1.0"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

// Toolchains:
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

// Minecraft configuration:
loom {
    log4jConfigs.from(file("extras/log4j2.xml"))
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
        }
    }
    runConfigs {
        "client" {
            if (SystemUtils.IS_OS_MAC_OSX) {
                // This argument causes a crash on macOS
                vmArgs.remove("-XstartOnFirstThread")
            }
        }
        remove(getByName("server"))
    }
    forge {
        pack200Provider.set(Pack200Adapter())
        mixinConfig("mixins.openutils.json")

    }
    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("mixins.openutils.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(sourceSets.main.flatMap { it.java.classesDirectory })
}

// Spotless Configuration:
spotless {
    java {
        importOrder()
        removeUnusedImports()
        eclipse().configFile("extras/eclipse-formatter.xml")
        formatAnnotations()
    }
}

// Dependencies:
repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")

    shade("com.google.code.gson:gson:2.13.2")
    shade("org.spongepowered:mixin:0.7.11-SNAPSHOT") {
        isTransitive = false
    }
    annotationProcessor("org.spongepowered:mixin:0.8.5-SNAPSHOT")

    runtimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.2.1")
}

// Tasks:
tasks {
    withType(JavaCompile::class) {
        options.encoding = "UTF-8"
    }

    withType(Jar::class) {
        archiveBaseName.set("OpenUtils")
        manifest.attributes.run {
            this["FMLCorePluginContainsFMLMod"] = "true"
            this["ForceLoadAsMod"] = "true"
            this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
            this["MixinConfigs"] = "mixins.openutils.json"
        }
    }

    withType(Jar::class) {
        archiveBaseName.set("OpenUtils")
        archiveVersion.set("1.0")

        manifest.attributes.run {
            this["MixinConfigs"] = "mixins.openutils.json"
            this["FMLCorePluginContainsFMLMod"] = "true"
            this["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
            this["ForceLoadAsMod"] = "true"
        }
    }

    val remapJar = named<RemapJarTask>("remapJar") {
        archiveClassifier.set("forge")
        from(shadowJar)
        input.set(shadowJar.get().archiveFile)
    }

    shadowJar {
        destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
        archiveClassifier.set("non-obfuscated-with-deps")
        configurations = listOf(shade)

        doLast {
            configurations.forEach {
                println("Copying dependencies into mod: ${it.files}")
            }
        }

        fun relocateInside(name: String) = relocate(name, "org.afterlike.openutils.lib.$name")

        relocateInside("com.google.gson")
    }

    jar {
        archiveClassifier.set("without-deps")
        destinationDirectory.set(layout.buildDirectory.dir("intermediates"))
    }

    assemble.get().dependsOn(remapJar)
}

