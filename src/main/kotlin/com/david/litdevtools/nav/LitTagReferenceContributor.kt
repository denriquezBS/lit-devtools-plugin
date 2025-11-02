package com.david.litdevtools.nav

import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.patterns.XmlPatterns
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.xml.util.HtmlUtil

class LitTagReferenceContributor : PsiReferenceContributor() {
  override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
    registrar.registerReferenceProvider(
      XmlPatterns.xmlTag(),
      object : PsiReferenceProvider() {
        override fun getReferencesByElement(element: PsiElement, ctx: ProcessingContext): Array<PsiReference> {
          val tag = element as? com.intellij.psi.xml.XmlTag ?: return PsiReference.EMPTY_ARRAY
          if (!HtmlUtil.isHtmlTag(tag)) return PsiReference.EMPTY_ARRAY
          val project = tag.project
          // Simple search: traverse project JS/TS files to find the class decorated with @customElement(tag.name)
          val scope = GlobalSearchScope.projectScope(project)
          val files = PsiShortNamesCache.getInstance(project).allFileNames
          // Heuristic: don't iterate everything; try local resolution (parent directory) first
          val candidates = mutableListOf<PsiElement>()
          val file = tag.containingFile
          val jsFiles = PsiTreeUtil.collectElements(file) { it is com.intellij.lang.javascript.psi.JSClass }
          jsFiles.mapNotNull { it as? com.intellij.lang.javascript.psi.JSClass }
            .mapNotNull { LitPsiUtil.tryBuildComponent(it) }
            .filter { it.tagName == tag.name }
            .mapTo(candidates) { it.jsClass }

          return if (candidates.isNotEmpty()) arrayOf(object : PsiReferenceBase<com.intellij.psi.xml.XmlTag>(tag, true) {
            override fun resolve(): PsiElement? = candidates.firstOrNull()
            override fun getVariants(): Array<Any> = candidates.mapNotNull { (it as? com.intellij.lang.javascript.psi.JSClass)?.name }.toTypedArray()
          }) else PsiReference.EMPTY_ARRAY
        }
      }
    )
  }
}
