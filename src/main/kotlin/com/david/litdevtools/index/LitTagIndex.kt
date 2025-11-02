package com.david.litdevtools.index

import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.lang.javascript.psi.JSClass
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.*

class LitTagIndex : StringStubIndexExtension<JSClass>() {
  companion object { val KEY = StubIndexKey.createIndexKey<String, JSClass>("lit.tag.index") }
  override fun getKey() = KEY
  override fun getVersion() = 1

  // Helper
  fun findByTag(tag: String, project: Project): Collection<JSClass> =
    StubIndex.getElements(KEY, tag, project, GlobalSearchScope.projectScope(project), JSClass::class.java)
}

// NOTE: Pour un MVP rapide on peut éviter la génération de stubs custom
// et résoudre à la volée via un visiteur PSI (moins perf mais suffisant).
object LitTagResolver {
  fun findCandidates(file: JSFile): Map<String, JSClass> {
    val found = mutableMapOf<String, JSClass>()
    file.accept(object : JSElementVisitor() {
      override fun visitJSClass(node: JSClass) {
        val comp = LitPsiUtil.tryBuildComponent(node) ?: return
        found[comp.tagName] = node
        super.visitJSClass(node)
      }
    })
    return found
  }
}
