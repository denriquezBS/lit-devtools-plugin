package com.david.litdevtools.structure

import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.ide.structureView.StructureViewModel
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile

class LitStructureViewBuilder : PsiStructureViewFactory {
  override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
    if (psiFile !is JSFile) return null
    return object : TreeBasedStructureViewBuilder() {
      override fun createStructureViewModel(editor: Editor?): StructureViewModel =
        object : com.intellij.ide.structureView.TextEditorBasedStructureViewModel(editor, psiFile) {
          override fun getRoot() = LitFileTreeElement(psiFile)
        }
    }
  }
}

class LitFileTreeElement(file: JSFile?) : PsiTreeElementBase<JSFile>(file) {
  override fun getPresentableText(): String = value?.name ?: ""
  override fun getChildrenBase(): MutableCollection<com.intellij.ide.structureView.StructureViewTreeElement> {
    val res = mutableListOf<com.intellij.ide.structureView.StructureViewTreeElement>()
    value?.let { jsFile ->
      // Find all TypeScript classes in the file
      jsFile.accept(object : com.intellij.lang.javascript.psi.JSRecursiveElementVisitor() {
        override fun visitTypeScriptClass(aClass: TypeScriptClass) {
          res += LitClassTreeElement(aClass)
        }
      })
    }
    return res
  }
}

class LitClassTreeElement(private val klass: TypeScriptClass) : PsiTreeElementBase<TypeScriptClass>(klass) {
  override fun getPresentableText(): String = klass.name ?: "(anonymous)"
  override fun getChildrenBase(): MutableCollection<com.intellij.ide.structureView.StructureViewTreeElement> =
    LitStructureElements.childrenFor(klass)
}
