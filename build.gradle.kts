plugins {
  id("org.jetbrains.intellij") version "1.17.2"
  kotlin("jvm") version "1.9.24"
}

repositories { mavenCentral() }

kotlin { jvmToolchain(17) }

intellij {
  // WebStorm = IU avec plugin JS, on cible la plate‑forme IntelliJ
  version.set("2024.2")
  type.set("IU")
  plugins.set(listOf("JavaScript")) // indispensable pour PSI JS/TS
}

tasks {
  patchPluginXml {
    sinceBuild.set("242")
    untilBuild.set(null as String?)
  }
}

dependencies {
  // rien de spécial : tout vient de la platform + plugin JavaScript
}


