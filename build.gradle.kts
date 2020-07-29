import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import io.qameta.allure.gradle.AllureExtension

group = "io.petstore"
version = 1.0
val allureVersion = "2.13.3"
val restAssuredVersion = "4.2.0"
val junit5Version = "5.3.0"

plugins {
    java
    maven
    kotlin("jvm") version "1.3.70"
    id("io.qameta.allure") version "2.8.1"
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

dependencies {
    //implementation("io.qameta.allure:allure-java-commons:$allureVersion")
    implementation("io.qameta.allure:allure-junit5:$allureVersion")
    //implementation("io.qameta.allure:allure-junit5-assert:$allureVersion")

    //implementation("io.qameta.allure:allure-assertj:$allureVersion")
    implementation("org.assertj:assertj-core:3.16.1")
    //implementation("io.qameta.allure:allure-rest-assured:$allureVersion")
    implementation("io.qameta.allure:allure-attachments:$allureVersion")

    implementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")
    implementation("io.rest-assured:rest-assured:$restAssuredVersion")

    implementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    implementation("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    implementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")

    implementation("org.slf4j:slf4j-simple:+")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:+")

    implementation("org.aspectj:aspectjrt:+")
    //implementation("org.assertj:assertj-core:+")

}

tasks.existing(Wrapper::class) {
    gradleVersion = "6.3"
    distributionType = Wrapper.DistributionType.ALL
}

/*tasks.withType(JavaCompile::class) {
    sourceCompatibility = "${JavaVersion.VERSION_1_8}"
    targetCompatibility = "${JavaVersion.VERSION_1_8}"
    options.encoding = "UTF-8"
}*/

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

configure<AllureExtension> {
    version = allureVersion
    allureJavaVersion = allureVersion

    autoconfigure = true
    aspectjweaver = true

    clean = true

    useJUnit5 {
        version = allureVersion
    }

}

tasks.withType(Test::class) {
    ignoreFailures = true
    useJUnitPlatform {

    }

    testLogging {
        events("passed", "skipped", "failed")
    }

    systemProperty("junit.jupiter.execution.parallel.enabled", "true")
    systemProperty("junit.jupiter.execution.parallel.config.strategy", "dynamic")
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
}

tasks.register("AllureReport") {
    doFirst {
        println("Delete old Allure report")
        delete("$buildDir/reports/allure-report")
    }
    finalizedBy("allureReport")
}