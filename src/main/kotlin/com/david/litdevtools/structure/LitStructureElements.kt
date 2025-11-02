package com.david.litdevtools.structure

import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.lang.javascript.psi.JSClass
import com.intellij.lang.javascript.psi.JSField
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.psi.PsiElement

object LitStructureElements {
  fun childrenFor(klass: JSClass): MutableCollection<StructureViewTreeElement> {
    val comp = LitPsiUtil.tryBuildComponent(klass) ?: return mutableListOf()
    val out = mutableListOf<StructureViewTreeElement>()

    out += Section("Properties", comp.properties.map { FieldItem(it.field, renderProp(it)) })
    out += Section("State", comp.states.map { FieldItem(it.field, renderState(it)) })
    out += Section("Private", comp.privates.map { FieldItem(it, it.name ?: "#") })
    out += Section("Methods", comp.methods.map { MethodItem(it, renderMethod(it)) })
    out += Section("Events", comp.events.map { TextItem(it) })
    out += Section("CSS", listOf(TextItem(if (comp.hasStyles) "styles" else "(none)")))
    return out
  }

  private fun renderProp(p: LitPsiUtil.LitProp) = buildString {
    append(p.name)
    append(": ")
    append(p.jsType ?: "any")
    p.defaultValue?.let { append(" = ").append(it) }
  }
  private fun renderState(p: LitPsiUtil.LitProp) = renderProp(p)
  private fun renderMethod(m: JSFunction) = buildString {
    append(m.name ?: "fn")
    append("(")
    append(m.parameterVariables.joinToString { it.name ?: "_" })
    append(")")
  }

  // —— Nodes ——
  private class Section(val title: String, val children: List<StructureViewTreeElement>) : StructureViewTreeElement {
    override fun getValue(): Any = title
    override fun navigate(requestFocus: Boolean) {}
    override fun canNavigate(): Boolean = false
    override fun canNavigateToSource(): Boolean = false
    override fun getAlphaSortKey(): String = title
    override fun getPresentation() = com.intellij.navigation.ItemPresentationProviders.getItemPresentation(this)
    override fun getChildren(): Array<StructureViewTreeElement> = children.toTypedArray()
  }

  private class FieldItem(val field: JSField, val label: String) : PsiTreeElementBase<JSField>(field) {
    override fun getPresentableText(): String = label
    override fun getChildrenBase(): MutableCollection<StructureViewTreeElement> = mutableListOf()
  }

  private class MethodItem(val fn: JSFunction, val label: String) : PsiTreeElementBase<JSFunction>(fn) {
    override fun getPresentableText(): String = label
    override fun getChildrenBase(): MutableCollection<StructureViewTreeElement> = mutableListOf()
  }

  private class TextItem(val label: String) : StructureViewTreeElement {
    override fun getValue(): Any = label
    override fun navigate(requestFocus: Boolean) {}
    override fun canNavigate(): Boolean = false
    override fun canNavigateToSource(): Boolean = false
    override fun getAlphaSortKey(): String = label
    override fun getPresentation() = com.intellij.navigation.ItemPresentationProviders.getItemPresentation(this)
    override fun getChildren(): Array<StructureViewTreeElement> = emptyArray()
  }
}
