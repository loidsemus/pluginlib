plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "me.loidsemus"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://repo.spongepowered.org/maven")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.1-R0.1-SNAPSHOT")
    implementation("org.spongepowered:configurate-hocon:3.6.1")
    testImplementation("junit:junit:4.12")
    testImplementation("org.assertj:assertj-core:3.16.1")
    testImplementation("org.spigotmc:spigot-api:1.16.1-R0.1-SNAPSHOT")
    testImplementation("commons-io:commons-io:2.6")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "pluginlib"
            from(components["java"])
            pom {
                name.set("pluginlib")
                description.set("Spigot commons")
            }
        }
    }
}
