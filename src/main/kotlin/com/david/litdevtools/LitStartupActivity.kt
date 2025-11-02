package com.david.litdevtools

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LitStartupActivity : ProjectActivity {
  private val LOG = Logger.getInstance(LitStartupActivity::class.java)
  
  override suspend fun execute(project: Project) {
    LOG.info("${LitConstants.PLUGIN_NAME}: Plugin starting up for project ${project.name}")
    
    // Show a notification to confirm the plugin is loaded
    NotificationGroupManager.getInstance()
      .getNotificationGroup(LitConstants.PLUGIN_NAME)
      .createNotification(
        "${LitConstants.PLUGIN_NAME} Plugin Active",
        "${LitConstants.PLUGIN_NAME} plugin v${LitConstants.PLUGIN_VERSION} is now active. Features: completion, navigation, structure view. Check 'Lit Inspector' tool window for diagnostics.",
        NotificationType.INFORMATION
      )
      .notify(project)
    
    LOG.info("${LitConstants.PLUGIN_NAME}: Startup notification sent")
  }
}
