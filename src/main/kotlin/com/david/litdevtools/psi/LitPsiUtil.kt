package com.david.litdevtools.psi

import com.intellij.lang.javascript.psi.*
import com.intellij.lang.javascript.psi.ecma6.*
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.psi.PsiElement

object LitPsiUtil {
  data class LitComponent(
    val tagName: String,
    val jsClass: JSClass,
    val properties: List<LitProp>,
    val states: List<LitProp>,
    val privates: List<JSField>,
    val methods: List<JSFunction>,
    val events: List<String>,
    val hasStyles: Boolean,
  )
  data class LitProp(
    val name: String,
    val attrName: String?,
    val jsType: String?,
    val defaultValue: String?,
    val field: JSField
  )

  fun isLitElement(klass: JSClass): Boolean {
    // class Foo extends LitElement
    return (klass.extendsList?.members?.any { it.text.contains("LitElement") } == true)
  }

  fun customElementTag(klass: JSClass): String? {
    // @customElement('my-tag')
    val attrs = klass.attributeList ?: return null
    val decorators = attrs.decorators ?: return null
    decorators.forEach { dec ->
      val expr = dec.expression
      if (expr is JSCallExpression) {
        val callee = expr.methodExpression?.text
        if (callee == "customElement" && expr.arguments.isNotEmpty()) {
          val arg = expr.arguments[0]
          return arg.text.trim('"', '\'')
        }
      }
    }
    return null
  }

  fun litMembers(klass: JSClass): Triple<List<LitProp>, List<LitProp>, List<JSField>> {
    val props = mutableListOf<LitProp>()
    val states = mutableListOf<LitProp>()
    val privs = mutableListOf<JSField>()

    klass.fields.forEach { f ->
      val attrList: JSAttributeList? = f.attributeList
      val decorators = attrList?.decorators ?: emptyArray()
      var isProperty = false
      var isState = false
      decorators.forEach { d ->
        val t = d.text
        if (t.startsWith("@property")) isProperty = true
        if (t.startsWith("@state")) isState = true
      }
      val defaultVal = f.initializer?.text
      val typeStr = f.jsType?.typeText
      val attrName = if (isProperty) f.name else null // simplified; can read options attribute:{} later

      if (isProperty) props += LitProp(f.name ?: "", attrName, typeStr, defaultVal, f)
      else if (isState) states += LitProp(f.name ?: "", null, typeStr, defaultVal, f)
      else if (f.isPrivate || f.name?.startsWith("_") == true) privs += f
    }
    return Triple(props, states, privs)
  }

  fun methods(klass: JSClass): List<JSFunction> = klass.functions.toList()

  fun events(klass: JSClass): List<String> {
    // Simple approach: searches for dispatchEvent(new CustomEvent('x')) in the class body
    val bodyText = klass.text
    val regex = Regex("dispatchEvent\\(new\\s+CustomEvent\\([\\'\"]([a-zA-Z0-9_-]+)[\\'\"]")
    return regex.findAll(bodyText).map { it.groupValues[1] }.distinct().toList()
  }

  fun hasStyles(klass: JSClass): Boolean =
    klass.fields.any { it.name == "styles" || it.text.contains("css`") }

  fun tryBuildComponent(klass: JSClass): LitComponent? {
    if (!isLitElement(klass)) return null
    val tag = customElementTag(klass) ?: return null
    val (props, states, privs) = litMembers(klass)
    return LitComponent(
      tagName = tag,
      jsClass = klass,
      properties = props,
      states = states,
      privates = privs,
      methods = methods(klass),
      events = events(klass),
      hasStyles = hasStyles(klass)
    )
  }
}
