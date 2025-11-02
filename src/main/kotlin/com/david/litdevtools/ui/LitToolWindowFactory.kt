package com.david.litdevtools.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import javax.swing.JLabel

class LitToolWindowFactory : ToolWindowFactory, DumbAware {
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    val content = toolWindow.contentManager.factory.createContent(JLabel("Lit Inspector"), "Lit Inspector", false)
    toolWindow.contentManager.addContent(content)
  }
}
