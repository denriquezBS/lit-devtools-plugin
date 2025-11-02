package com.david.litdevtools.nav

import com.david.litdevtools.index.LitTagResolver
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FilenameIndex
import com.intellij.xml.util.HtmlUtil
import com.intellij.lang.javascript.psi.JSFile

class LitTagReferenceContributor : PsiReferenceContributor() {
  override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
    registrar.registerReferenceProvider(
      XmlPatterns.xmlTag(),
      object : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, context: com.intellij.util.ProcessingContext): Array<PsiReference> {
          val tag = element as? com.intellij.psi.xml.XmlTag ?: return PsiReference.EMPTY_ARRAY
          if (!HtmlUtil.isHtmlTag(tag)) return PsiReference.EMPTY_ARRAY
          val project = tag.project
          val tagName = tag.name
          
          // Search across all JavaScript/TypeScript files in the project
          val scope = GlobalSearchScope.projectScope(project)
          val candidates = mutableListOf<PsiElement>()
          val psiManager = PsiManager.getInstance(project)
          
          // Find all JS/TS files by extension
          listOf("ts", "js", "tsx", "jsx", "mjs").forEach { ext ->
            FilenameIndex.getAllFilesByExt(project, ext, scope).forEach { vf ->
              val psiFile = psiManager.findFile(vf) as? JSFile ?: return@forEach
              val components = LitTagResolver.findCandidates(psiFile)
              components[tagName]?.let { candidates.add(it) }
            }
          }

          return if (candidates.isNotEmpty()) arrayOf(object : PsiReferenceBase<com.intellij.psi.xml.XmlTag>(tag, true) {
            override fun resolve(): PsiElement? = candidates.firstOrNull()
            override fun getVariants(): Array<Any> = candidates.mapNotNull { (it as? com.intellij.lang.javascript.psi.ecma6.TypeScriptClass)?.name }.toTypedArray()
          }) else PsiReference.EMPTY_ARRAY
        }
      }
    )
  }
}
