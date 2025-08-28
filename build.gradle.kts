
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "2.2.10"
	id("fabric-loom") version "1.11-SNAPSHOT"
	id("maven-publish")
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
	archivesName.set(project.property("archives_base_name") as String)
}

loom {
	accessWidenerPath = file("src/main/resources/wisienka.accesswidener")
}

val targetJavaVersion = 23
java {
	toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
	// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
	// if it is present.
	// If you remove this line, sources will not be generated.
	withSourcesJar()
}


fabricApi {
	configureDataGeneration {
		client = true
	}
}

repositories {
	// Add repositories to retrieve artifacts from in here.
	// You should only use this when depending on other mods because
	// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
	// See https://docs.gradle.org/current/userguide/declaring_repositories.html
	// for more information about repositories.


	flatDir {
		dirs("run/mods")
	}

	maven("https://maven.parchmentmc.org") // Parchment mappings

	maven("https://mvn.devos.one/releases") // Porting Lib releases
	maven("https://mvn.devos.one/snapshots") // Create and several dependencies
	maven("https://modmaven.dev/") // Flywheel
	maven("https://maven.jamieswhiteshirt.com/libs-release") // Reach Entity Attributes
	maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven") // Forge Config API Port

	maven { // Fabric ASM for Porting Lib
		url = uri("https://jitpack.io/")
		content {
			includeGroupByRegex("com.github.*")
		}
	}

	maven("https://maven.shedaniel.me") // Cloth Config, REI
	maven("https://maven.blamejared.com") // JEI

	maven("https://maven.terraformersmc.com/releases") // Mod Menu, EMI

	maven (
		"https://maven.createmod.net/"
	)

	exclusiveContent {
		forRepository {
			maven (
				url = "https://api.modrinth.com/maven"
			)
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}

	maven("https://maven.ladysnake.org/releases")

	maven ("https://maven.blamejared.com")
}

configurations.configureEach {
	resolutionStrategy {
		// make sure the desired version of loader is used. Sometimes old versions are pulled in transitively.
		force("net.fabricmc:fabric-loader:${project.property("loader_version")}")
	}
}

dependencies {
	// To change the versions see the gradle.properties file
	minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
	mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

	modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_api_version")}")

	// Setup
//	mappings(loom.layered {
//		officialMojangMappings { nameSyntheticMembers = false }
//		parchment(
//			"org.parchmentmc.data:parchment-" +
//					"${project.property("minecraft_version")}:" +
//					"${project.property("parchment_version")}@zip")
//	})

	// dependencies

	// Create - dependencies are added transitively
//	modImplementation(
//		"com.simibubi.create:create-fabric-" +
//				"${project.property("minecraft_version")}:" +
//				"${project.property("create_version")}")

	// Development QOL
//	modLocalRuntime("com.terraformersmc:modmenu:${project.property("modmenu_version")}")

	// Recipe Viewers - Create Fabric supports JEI, REI, and EMI.
	// See root gradle.properties to choose which to use at runtime.
//	when (project.property("recipe_viewer").toString().lowercase()) {
//		"jei" -> modLocalRuntime("mezz.jei:jei-" +
//				"${project.property("minecraft_version")}-fabric:" +
//				"${project.property("jei_version")}")
//		"rei" -> modLocalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:${project.property("rei_version")}")
//		"emi" -> modLocalRuntime("dev.emi:emi-fabric:${project.property("emi_version")}")
//		"disabled" -> {}
//		else -> println("Unknown recipe viewer specified: " +
//				"${project.property("recipe_viewer")}. Must be JEI, REI, EMI, or disabled.")
//	}
	// if you would like to add integration with them, uncomment them here.
	modCompileOnly("mezz.jei:jei-${project.property("minecraft_version")}-fabric:${project.property("jei_version")}")
	modCompileOnly("mezz.jei:jei-${project.property("minecraft_version")}-common:${project.property("jei_version")}")
	modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${project.property("rei_version")}")
	modCompileOnly("me.shedaniel:RoughlyEnoughItems-default-plugin-fabric:${project.property("rei_version")}")
	modCompileOnly("dev.emi:emi-fabric:${project.property("emi_version")}")

//	modApi(
//		"dev.engine-room.flywheel:flywheel-fabric-api-" +
//				"${project.property("minecraft_version")}:" +
//				"${project.property("flywheel_version")}")
//	modImplementation(
//		"dev.engine-room.flywheel:flywheel-fabric-" +
//				"${project.property("minecraft_version")}:" +
//				"${project.property("flywheel_version")}")

//	fun DependencyHandlerScope.modrinth(name: String) {
//		if (!name.isEmpty())
//			modCompileOnly("maven.modrinth:$name:${project.property("${name}_version")}")
//	}
//
//	fun DependencyHandlerScope.modrinthLatest(name: String) {
//		if (!name.isEmpty())
//			modCompileOnly("maven.modrinth:$name:+")
//	}
//
//	modrinth("amarite")
//	modrinth("arsenal")
//	modrinth("blast")
//	modrinth("createaddition")
//	modrinth("create-pantographs-and-wires")
//	modrinth("create-steam-n-rails")
//	modrinth("enchancement")
//	modrinth("impersonate")
//	modrinth("invmove")
//	modrinth("jade")
//	modrinth("numismatics")
//	modrinth("pick-your-poison")
//	modrinth("ratatouille")
//	modrinth("simple-voice-chat")
//	modrinth("trinkets")
//	modrinth("wakes")
//	modrinth("")
//
//	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${project.property("cardinal-components-api_version")}")
//	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${project.property("cardinal-components-api_version")}")
//	modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-block:${project.property("cardinal-components-api_version")}")
//
//	//modrinthLatest("tooltipfix")
//	modrinth("satin-api")
//	modrinth("geckolib")
//	modrinth("dragonlib")
//	modrinth("fabric-permissions-api")
//	modrinth("architectury-api")
//	modrinth("midnightlib")
//	modrinthLatest("")

	val mods = File("./run/mods")

	for (file in mods.listFiles()) {
		if (file.extension == "jar") modCompileOnly(":${file.nameWithoutExtension}:")
	}

	modImplementation("foundry.veil:Veil-fabric-" +
			"${project.property("minecraft_version")}:" +
			"${project.property("veil_version")}") {
		exclude(group = "maven.modrinth")
		exclude(group = "me.fallenbreath")
	}

	implementation("de.mkammerer:argon2-jvm:2.12")
}

tasks.processResources {
	inputs.property("version", project.version)
	inputs.property("minecraft_version", project.property("minecraft_version"))
	inputs.property("loader_version", project.property("loader_version"))
	inputs.property("create_version", project.property("create_version"))
	filteringCharset = "UTF-8"

	filesMatching("fabric.mod.json") {
		expand(
			"version" to project.version,
			"minecraft_version" to project.property("minecraft_version"),
			"loader_version" to project.property("loader_version"),
			"kotlin_loader_version" to project.property("kotlin_loader_version"),
			"create_version" to project.property("create_version")
		)
	}
}

tasks.withType<JavaCompile>().configureEach {
	// ensure that the encoding is set to UTF-8, no matter what the system default is
	// this fixes some edge cases with special characters not displaying correctly
	// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
	// If Javadoc is generated, this must be specified in that task too.
	options.encoding = "UTF-8"
	options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_${project.base.archivesName}" }
	}
}

// configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = project.property("archives_base_name") as String
			from(components["java"])
		}
	}

	// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
	repositories {
		// Add repositories to publish to here.
		// Notice: This block does NOT have the same function as the block in the top level.
		// The repositories here will be used for publishing your artifact, not for
		// retrieving dependencies.
	}
}
