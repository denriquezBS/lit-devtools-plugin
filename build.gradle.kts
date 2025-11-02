plugins {
  id("org.jetbrains.intellij.platform") version "2.10.3"
  kotlin("jvm") version "2.0.21"
}

repositories {
  mavenCentral()
  intellijPlatform { defaultRepositories() }
}

kotlin { jvmToolchain(17) }

intellijPlatform {
  buildSearchableOptions = false
  instrumentCode = true
  pluginConfiguration {
    ideaVersion { sinceBuild = "242"; untilBuild = provider { null } }
  }
}

dependencies {
  intellijPlatform {
    intellijIdeaUltimate("2024.2")
    bundledPlugin("JavaScript")
    pluginVerifier()
    zipSigner()
  }
  testImplementation("junit:junit:4.13.2")
}

tasks.test { useJUnit() }
