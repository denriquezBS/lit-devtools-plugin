package com.david.litdevtools.completion

import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.PsiElement
import com.intellij.util.ProcessingContext
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlTag

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
    val file = tag.containingFile
    val jsClasses = com.intellij.psi.util.PsiTreeUtil.collectElements(file) { it is com.intellij.lang.javascript.psi.ecma6.TypeScriptClass }
    jsClasses.forEach {
      val klass = it as com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
      val comp = LitPsiUtil.tryBuildComponent(klass) ?: return@forEach
      if (comp.tagName == tag.name) return klass
    }
    return null
  }
}
