package com.david.litdevtools

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LitStartupActivity : ProjectActivity {
  private val LOG = Logger.getInstance(LitStartupActivity::class.java)
  
  override suspend fun execute(project: Project) {
    LOG.info("Lit DevTools: Plugin starting up for project ${project.name}")
    
    // Show a notification to confirm the plugin is loaded
    NotificationGroupManager.getInstance()
      .getNotificationGroup("Lit DevTools")
      .createNotification(
        "Lit DevTools Plugin Active",
        "Lit DevTools plugin v0.1.0 is now active. Features: completion, navigation, structure view. Check 'Lit Inspector' tool window for diagnostics.",
        NotificationType.INFORMATION
      )
      .notify(project)
    
    LOG.info("Lit DevTools: Startup notification sent")
  }
}
