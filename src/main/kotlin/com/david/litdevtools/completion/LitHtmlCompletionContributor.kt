package com.david.litdevtools.completion

import com.david.litdevtools.psi.LitPsiUtil
import com.david.litdevtools.index.LitTagResolver
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FilenameIndex
import com.intellij.util.ProcessingContext
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag
import com.intellij.lang.javascript.psi.JSFile

class LitHtmlCompletionContributor : CompletionContributor() {
  init {
    // Attributes for a Lit tag
    extend(CompletionType.BASIC, XmlPatterns.xmlAttribute().withParent(XmlPatterns.xmlTag()),
      object : CompletionProvider<CompletionParameters>() {
        override fun addCompletions(p: CompletionParameters, ctx: ProcessingContext, r: CompletionResultSet) {
          val attr = p.position.parent as? XmlAttribute ?: return
          val tag = attr.parent as? XmlTag ?: return
          val klass = resolveTagToClass(tag) ?: return
          val comp = LitPsiUtil.tryBuildComponent(klass) ?: return

          comp.properties.forEach { prop ->
            val le = LookupElementBuilder.create(prop.attrName ?: prop.name)
              .withTypeText(prop.jsType ?: "any", true)
              .withTailText(prop.defaultValue?.let { " = $it" } ?: "", true)
            r.addElement(le)
          }
          // Events (simple): suggest @event and onInput-like handlers
          comp.events.forEach { ev -> r.addElement(LookupElementBuilder.create("@${ev}")) }
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
      com.intellij.psi.search.FilenameIndex.getAllFilesByExt(project, ext, scope).forEach { vf ->
        val psiFile = psiManager.findFile(vf) as? JSFile ?: return@forEach
        val components = LitTagResolver.findCandidates(psiFile)
        components[tagName]?.let { return it }
      }
    }
    return null
  }
}
