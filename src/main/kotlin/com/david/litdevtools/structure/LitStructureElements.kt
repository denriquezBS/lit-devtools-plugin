package com.david.litdevtools.structure

import com.david.litdevtools.psi.LitPsiUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.JSField
import com.intellij.lang.javascript.psi.JSFunction
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.PsiElement
import javax.swing.Icon

object LitStructureElements {
  fun childrenFor(klass: TypeScriptClass): MutableCollection<StructureViewTreeElement> {
    val comp = LitPsiUtil.tryBuildComponent(klass) ?: return mutableListOf()
    val out = mutableListOf<StructureViewTreeElement>()

    // Only add sections that have content
    if (comp.properties.isNotEmpty()) {
      out += Section("Properties", comp.properties.map { FieldItem(it.field, renderProp(it)) }, AllIcons.Nodes.Property)
    }
    if (comp.states.isNotEmpty()) {
      out += Section("State", comp.states.map { FieldItem(it.field, renderState(it)) }, AllIcons.Nodes.Field)
    }
    if (comp.privates.isNotEmpty()) {
      out += Section("Private", comp.privates.map { FieldItem(it, it.name ?: "#") }, AllIcons.Nodes.FieldPK)
    }
    if (comp.methods.isNotEmpty()) {
      out += Section("Methods", comp.methods.map { MethodItem(it, renderMethod(it)) }, AllIcons.Nodes.Method)
    }
    if (comp.events.isNotEmpty()) {
      out += Section("Events", comp.events.map { TextItem(it, AllIcons.Nodes.Method) }, AllIcons.Nodes.Method)
    }
    out += Section("CSS", listOf(TextItem(if (comp.hasStyles) "styles defined" else "no styles", AllIcons.FileTypes.Css)), AllIcons.FileTypes.Css)
    
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
  private class Section(val title: String, val children: List<StructureViewTreeElement>, val icon: Icon?) : StructureViewTreeElement {
    override fun getValue(): Any = title
    override fun navigate(requestFocus: Boolean) {}
    override fun canNavigate(): Boolean = false
    override fun canNavigateToSource(): Boolean = false
    override fun getPresentation(): ItemPresentation = object : ItemPresentation {
      override fun getPresentableText(): String = "$title (${children.size})"
      override fun getLocationString(): String? = null
      override fun getIcon(unused: Boolean): Icon? = icon
    }
    override fun getChildren(): Array<StructureViewTreeElement> = children.toTypedArray()
  }

  private class FieldItem(val field: JSField, val label: String) : PsiTreeElementBase<JSField>(field) {
    override fun getPresentableText(): String = label
    override fun getChildrenBase(): MutableCollection<StructureViewTreeElement> = mutableListOf()
    override fun getIcon(open: Boolean): Icon? = AllIcons.Nodes.Field
  }

  private class MethodItem(val fn: JSFunction, val label: String) : PsiTreeElementBase<JSFunction>(fn) {
    override fun getPresentableText(): String = label
    override fun getChildrenBase(): MutableCollection<StructureViewTreeElement> = mutableListOf()
    override fun getIcon(open: Boolean): Icon? = AllIcons.Nodes.Method
  }

  private class TextItem(val label: String, val icon: Icon?) : StructureViewTreeElement {
    override fun getValue(): Any = label
    override fun navigate(requestFocus: Boolean) {}
    override fun canNavigate(): Boolean = false
    override fun canNavigateToSource(): Boolean = false
    override fun getPresentation(): ItemPresentation = object : ItemPresentation {
      override fun getPresentableText(): String = label
      override fun getLocationString(): String? = null
      override fun getIcon(unused: Boolean): Icon? = icon
    }
    override fun getChildren(): Array<StructureViewTreeElement> = emptyArray()
  }
}
