import org.gradle.kotlin.dsl.registering

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.openapi.generator") version "7.10.0"
	id("com.diffplug.spotless") version "6.17.0"

}

spotless {
	java {
		googleJavaFormat("1.15.0")
		target("src/**/*.java")
	}
	kotlin {
		ktfmt()
		ktlint("0.46.0")
		target("src/**/*.kt")
	}
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.openapitools:openapi-generator-gradle-plugin:7.10.0")
	}
}

sourceSets {
	main {
		java {
			srcDirs("$rootDir/openapi/src/main/kotlin",
				"$rootDir/openapi/src/main/java")
		}
	}
}

repositories {
	mavenCentral()
}

dependencies {
	compileOnly("jakarta.platform:jakarta.jakartaee-api:10.0.0")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("io.swagger.core.v3:swagger-annotations-jakarta:2.2.25")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.glassfish.jersey.core:jersey-server:3.0.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("io.rest-assured:rest-assured:5.3.0")
	testImplementation("io.rest-assured:kotlin-extensions:5.5.0")
	testImplementation("jakarta.platform:jakarta.jakartaee-api:10.0.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

openApiGenerate {
	generatorName.set("spring")
	generateApiTests.set(true)
	generateModelTests.set(true)
	inputSpec.set("$rootDir/src/main/resources/api.yml")
	outputDir.set("$rootDir/openapi")
	configOptions.set(mapOf("useSpringBoot3" to "true",
		"skipDefaultInterface" to "true", "generateSupportingFiles" to "false",
		"interfaceOnly" to "true", "serializableModel" to "true"))
	apiPackage.set("com.example.api.controller")
	modelPackage.set("com.example.api.dto")
	modelNameSuffix.set("Dto")
}

tasks.named("compileKotlin") {
	dependsOn(tasks.named("spotlessKotlinApply"), tasks.named("openApiGenerate"))
}
tasks.named("compileJava") {
	dependsOn(tasks.named("spotlessJavaApply"), tasks.named("openApiGenerate"))
}
tasks.named("spotlessJava") {
	dependsOn(tasks.named("openApiGenerate"))
}
tasks.named("spotlessKotlin") {
	dependsOn(tasks.named("openApiGenerate"))
}

tasks.register("integrationTest", Test::class) {
	description = "Runs integration tests."
	group = "verification"
}