package com.david.litdevtools

import com.intellij.openapi.options.Configurable
import javax.swing.*

class LitSettings : Configurable {
  private val panel = JPanel()
  private val enable = JCheckBox("Enable Lit DevTools for this project", true)
  override fun getDisplayName(): String = "Lit DevTools"
  override fun createComponent(): JComponent { panel.add(enable); return panel }
  override fun isModified(): Boolean = false
  override fun apply() {}
}
