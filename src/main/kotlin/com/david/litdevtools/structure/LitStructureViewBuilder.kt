package com.david.litdevtools.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

class LitStructureViewBuilder : TreeBasedStructureViewBuilder() {
  override fun createStructureViewModel(editor: Editor?): StructureViewModel =
    object : com.intellij.ide.structureView.TextEditorBasedStructureViewModel(editor, editor?.psiFile) {
      override fun getRoot() = LitFileTreeElement(editor?.psiFile as? JSFile)
      override fun isAlwaysShowsPlus(element: com.intellij.ide.structureView.StructureViewTreeElement) = true
      override fun isAlwaysLeaf(element: com.intellij.ide.structureView.StructureViewTreeElement) = false
    }
}

class LitFileTreeElement(file: JSFile?) : PsiTreeElementBase<JSFile>(file) {
  override fun getPresentableText(): String = value?.name ?: ""
  override fun getChildrenBase(): MutableCollection<com.intellij.ide.structureView.StructureViewTreeElement> {
    val res = mutableListOf<com.intellij.ide.structureView.StructureViewTreeElement>()
    value?.classes?.forEach { jsClass -> res += LitClassTreeElement(jsClass) }
    return res
  }
}

class LitClassTreeElement(private val klass: TypeScriptClass) : PsiTreeElementBase<TypeScriptClass>(klass) {
  override fun getPresentableText(): String = klass.name ?: "(anonymous)"
  override fun getChildrenBase(): MutableCollection<com.intellij.ide.structureView.StructureViewTreeElement> =
    LitStructureElements.childrenFor(klass)
}
