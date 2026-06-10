plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("plugin.serialization") version "2.0.21"
    application
}

group = "com.arcanjodev"
version = "0.1.0"

val ktorVersion = "3.0.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

// Debug tasks: run the application with JDWP enabled so you can attach a debugger.
// Use: .\gradlew.bat runDebug    -> JVM will suspend and wait for debugger at port 5005
//      .\gradlew.bat runDebugNoSuspend -> JVM starts immediately and debugger can attach to port 5005
tasks.register<JavaExec>("runDebug") {
    group = "application"
    description = "Run application with JDWP debug enabled (suspend until debugger attaches)"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.ktor.server.netty.EngineMain")
    jvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005")
    standardInput = System.`in`
}

tasks.register<JavaExec>("runDebugNoSuspend") {
    group = "application"
    description = "Run application with JDWP debug enabled (does not suspend)"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.ktor.server.netty.EngineMain")
    jvmArgs = listOf("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005")
    standardInput = System.`in`
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
}

tasks.test {
    useJUnitPlatform()
}
