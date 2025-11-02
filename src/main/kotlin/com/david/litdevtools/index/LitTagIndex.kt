package com.david.litdevtools.index

import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*

class LitTagIndex : StringStubIndexExtension<TypeScriptClass>() {
  companion object { val KEY = StubIndexKey.createIndexKey<String, TypeScriptClass>("lit.tag.index") }
  override fun getKey() = KEY
  override fun getVersion() = 1

  // Helper
  fun findByTag(tag: String, project: Project): Collection<TypeScriptClass> =
    StubIndex.getElements(KEY, tag, project, GlobalSearchScope.projectScope(project), TypeScriptClass::class.java)
}

// NOTE: For a quick MVP we can avoid generating custom stubs
// and resolve on-the-fly via a PSI visitor (less performant but sufficient).
object LitTagResolver {
  fun findCandidates(file: JSFile): Map<String, TypeScriptClass> {
    val found = mutableMapOf<String, TypeScriptClass>()
    file.accept(object : JSElementVisitor() {
      override fun visitTypeScriptClass(node: TypeScriptClass) {
        val comp = LitPsiUtil.tryBuildComponent(node) ?: return
        found[comp.tagName] = node
        super.visitTypeScriptClass(node)
      }
    })
    return found
  }
}
