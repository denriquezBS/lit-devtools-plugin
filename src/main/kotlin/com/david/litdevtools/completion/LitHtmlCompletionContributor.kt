package com.david.litdevtools.completion

import com.david.litdevtools.psi.LitPsiUtil
import com.david.litdevtools.index.LitTagResolver
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FilenameIndex
import com.intellij.util.ProcessingContext
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.psi.xml.XmlTokenType
import com.intellij.lang.javascript.psi.JSFile

class LitHtmlCompletionContributor : CompletionContributor() {
  private val LOG = Logger.getInstance(LitHtmlCompletionContributor::class.java)
  
  init {
    LOG.info("Lit DevTools: LitHtmlCompletionContributor initialized")
    // Match when typing attribute names in XML/HTML tags
    // This pattern matches tokens inside an XML tag that could be attribute names
    extend(
      CompletionType.BASIC,
      PlatformPatterns.psiElement(XmlTokenType.XML_NAME).withParent(XmlPatterns.xmlAttribute()),
      object : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(p: CompletionParameters, ctx: ProcessingContext, r: CompletionResultSet) {
          val attr = p.position.parent as? XmlAttribute ?: return
          val tag = attr.parent as? XmlTag ?: return
          
          LOG.info("Lit DevTools: Attempting completion for tag <${tag.name}>")
          
          val klass = resolveTagToClass(tag) ?: run {
            LOG.info("Lit DevTools: No Lit component found for tag <${tag.name}>")
            return
          }
          val comp = LitPsiUtil.tryBuildComponent(klass) ?: return

          LOG.info("Lit DevTools: Providing ${comp.properties.size} property completions and ${comp.events.size} event completions for <${tag.name}>")

          comp.properties.forEach { prop ->
            val le = LookupElementBuilder.create(prop.attrName ?: prop.name)
              .withTypeText(prop.jsType ?: "any", true)
              .withTailText(prop.defaultValue?.let { " = $it" } ?: "", true)
              .withIcon(AllIcons.Nodes.Property)
            r.addElement(le)
          }
          // Events (simple): suggest @event and onInput-like handlers
          comp.events.forEach { ev -> 
            val le = LookupElementBuilder.create("@${ev}")
              .withTypeText("event", true)
              .withIcon(AllIcons.Nodes.Method)
            r.addElement(le)
          }
        }
      })
  }

  private fun resolveTagToClass(tag: XmlTag): com.intellij.lang.javascript.psi.ecma6.TypeScriptClass? {
    val project = tag.project
    val tagName = tag.name
    val scope = GlobalSearchScope.projectScope(project)
    val psiManager = PsiManager.getInstance(project)
    
    // Search across all JavaScript/TypeScript files by extension
    listOf("ts", "js", "tsx", "jsx", "mjs").forEach { ext ->
      FilenameIndex.getAllFilesByExt(project, ext, scope).forEach { vf ->
        val psiFile = psiManager.findFile(vf) as? JSFile ?: return@forEach
        val components = LitTagResolver.findCandidates(psiFile)
        components[tagName]?.let { return it }
      }
    }
    return null
  }
}
