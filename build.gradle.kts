plugins {
  id("org.jetbrains.intellij") version "1.17.2"
  kotlin("jvm") version "1.9.24"
}

repositories { mavenCentral() }

kotlin { jvmToolchain(17) }

intellij {
  // WebStorm = IU with JS plugin, we target the IntelliJ platform
  version.set("2024.2")
  type.set("IU")
  plugins.set(listOf("JavaScript")) // essential for JS/TS PSI
}

tasks {
  patchPluginXml {
    sinceBuild.set("242")
    untilBuild.set(null as String?)
  }
}

dependencies {
  // nothing special: everything comes from the platform + JavaScript plugin
}


