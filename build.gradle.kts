plugins {
  id("org.jetbrains.intellij.platform") version "2.10.3"
  kotlin("jvm") version "2.0.21"
}

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

kotlin { jvmToolchain(17) }

intellijPlatform {
  // WebStorm = IU with JS plugin, we target the IntelliJ platform
  buildSearchableOptions = false
  instrumentCode = true

  pluginConfiguration {
    ideaVersion {
      sinceBuild = "242"
      untilBuild = provider { null }
    }
  }
}

dependencies {
  intellijPlatform {
    webstorm("2025.2")
    bundledPlugin("JavaScript")

    pluginVerifier()
    zipSigner()
    instrumentationTools()
  }
}














