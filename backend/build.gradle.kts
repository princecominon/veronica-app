plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    application
}

group = "com.veronica"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // Your existing server libraries
    implementation("io.ktor:ktor-server-core:3.5.2")
    implementation("io.ktor:ktor-server-netty:3.5.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.5.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.2")
    implementation("io.ktor:ktor-server-cors:3.5.2")
    
    // 1. The HTTP Client libraries (so Ktor can act like a browser)
    implementation("io.ktor:ktor-client-core:3.5.2")
    implementation("io.ktor:ktor-client-okhttp:3.5.2")
    
    // 2. The Dotenv library (to read your .env file)
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    
    testImplementation("io.ktor:ktor-server-test-host:3.5.2")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.veronica.ApplicationKt")
}

kotlin {
    jvmToolchain(21)
}