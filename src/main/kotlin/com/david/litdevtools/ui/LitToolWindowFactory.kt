package com.david.litdevtools.ui

import com.david.litdevtools.LitConstants
import com.david.litdevtools.index.LitTagResolver
import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.psi.PsiManager
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.*

class LitToolWindowFactory : ToolWindowFactory, DumbAware {
  private val LOG = Logger.getInstance(LitToolWindowFactory::class.java)
  
  override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
    LOG.info("Lit DevTools: Creating tool window content")
    val panel = LitInspectorPanel(project)
    val content = toolWindow.contentManager.factory.createContent(panel, "Status", false)
    toolWindow.contentManager.addContent(content)
  }
}

class LitInspectorPanel(private val project: Project) : JPanel(BorderLayout()) {
  private val LOG = Logger.getInstance(LitInspectorPanel::class.java)
  private val textArea = JTextArea()
  
  init {
    textArea.isEditable = false
    textArea.font = Font("Monospaced", Font.PLAIN, 12)
    
    val refreshButton = JButton("Refresh")
    refreshButton.addActionListener { refresh() }
    
    val topPanel = JPanel(BorderLayout())
    topPanel.add(JLabel(" ${LitConstants.PLUGIN_NAME} Plugin Status"), BorderLayout.WEST)
    topPanel.add(refreshButton, BorderLayout.EAST)
    
    add(topPanel, BorderLayout.NORTH)
    add(JBScrollPane(textArea), BorderLayout.CENTER)
    
    refresh()
  }
  
  private fun refresh() {
    LOG.info("${LitConstants.PLUGIN_NAME}: Refreshing inspector panel")
    val sb = StringBuilder()
    sb.appendLine("=== ${LitConstants.PLUGIN_NAME} Plugin v${LitConstants.PLUGIN_VERSION} ===")
    sb.appendLine()
    sb.appendLine("Status: ACTIVE ✓")
    sb.appendLine("Project: ${project.name}")
    sb.appendLine()
    
    // Get currently open file
    val currentFile = FileEditorManager.getInstance(project).selectedTextEditor?.let { editor ->
      PsiManager.getInstance(project).findFile(editor.virtualFile)
    }
    
    if (currentFile is JSFile) {
      sb.appendLine("Current File: ${currentFile.name}")
      sb.appendLine()
      
      val components = LitTagResolver.findCandidates(currentFile)
      if (components.isEmpty()) {
        sb.appendLine("No Lit components found in current file.")
      } else {
        sb.appendLine("Found ${components.size} Lit component(s):")
        sb.appendLine()
        
        components.values.forEach { klass ->
          val comp = LitPsiUtil.tryBuildComponent(klass)
          if (comp != null) {
            sb.appendLine("Component: <${comp.tagName}>")
            sb.appendLine("  Class: ${klass.name}")
            sb.appendLine("  Properties: ${comp.properties.size}")
            comp.properties.forEach { prop ->
              sb.appendLine("    - ${prop.name}: ${prop.jsType ?: "any"}")
            }
            sb.appendLine("  State: ${comp.states.size}")
            comp.states.forEach { state ->
              sb.appendLine("    - ${state.name}: ${state.jsType ?: "any"}")
            }
            sb.appendLine("  Methods: ${comp.methods.size}")
            sb.appendLine("  Events: ${comp.events.size}")
            comp.events.forEach { event ->
              sb.appendLine("    - ${event}")
            }
            sb.appendLine("  Has Styles: ${comp.hasStyles}")
            sb.appendLine()
          }
        }
      }
    } else {
      sb.appendLine("Current File: ${currentFile?.name ?: "None"}")
      sb.appendLine()
      sb.appendLine("Open a TypeScript/JavaScript file containing Lit components")
      sb.appendLine("to see component details here.")
    }
    
    sb.appendLine()
    sb.appendLine("=== Features ===")
    sb.appendLine("✓ Autocompletion - Type attributes on Lit tags in HTML")
    sb.appendLine("✓ Navigation - Ctrl/Cmd-Click on tags to jump to class")
    sb.appendLine("✓ Structure View - Alt/Cmd+7 to see organized component view")
    sb.appendLine()
    sb.appendLine("=== Logging ===")
    sb.appendLine("Check IDE log for detailed diagnostics:")
    sb.appendLine("Help → Show Log in Explorer/Finder")
    sb.appendLine("Search for '${LitConstants.PLUGIN_NAME}' to see plugin activity")
    
    textArea.text = sb.toString()
    LOG.info("${LitConstants.PLUGIN_NAME}: Inspector panel refreshed")
  }
}
