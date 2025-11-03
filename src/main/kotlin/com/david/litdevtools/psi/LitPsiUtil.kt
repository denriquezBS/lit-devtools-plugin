package com.david.litdevtools.psi

import com.intellij.lang.javascript.psi.*
import com.intellij.lang.javascript.psi.ecma6.*
import com.intellij.lang.javascript.psi.ecmal4.JSAttributeList
import com.intellij.openapi.diagnostic.Logger

object LitPsiUtil {
  private val LOG = Logger.getInstance(LitPsiUtil::class.java)
  data class LitComponent(
    val tagName: String,
    val jsClass: TypeScriptClass,
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

  fun isLitElement(klass: TypeScriptClass): Boolean {
    // Check if class extends LitElement directly or indirectly
    var current = klass.extendsList
    while (current != null) {
      current.members.forEach { member ->
        val text = member.text
        if (text.contains("LitElement") || text.contains("ReactiveElement")) {
          return true
        }
      }
      // Try to follow the inheritance chain
      break // For now, just check direct parent
    }
    
    // Also check if the class has Lit-specific markers like @property, @state, or render() method
    val hasLitDecorators = klass.fields.any { field ->
      field.attributeList?.decorators?.any { dec ->
        val t = dec.text
        t.startsWith("@property") || t.startsWith("@state")
      } == true
    }
    
    val hasRenderMethod = klass.functions.any { it.name == "render" }
    
    return hasLitDecorators || hasRenderMethod
  }

  fun customElementTag(klass: TypeScriptClass): String? {
    // First try @customElement decorator
    val attrs = klass.attributeList
    if (attrs != null) {
      attrs.decorators.forEach { dec ->
        val expr = dec.expression
        if (expr is JSCallExpression) {
          val callee = expr.methodExpression?.text
          if (callee == "customElement" && expr.arguments.isNotEmpty()) {
            val arg = expr.arguments[0]
            return arg.text.trim('"', '\'')
          }
        }
      }
    }
    
    // Also try static tagName property or class name to kebab-case
    klass.fields.firstOrNull { it.name == "tagName" }?.let { field ->
      field.initializer?.text?.trim('"', '\'')?.let { return it }
    }
    
    // Try to derive from class name (e.g., MyElement -> my-element)
    // This is a fallback for components without @customElement
    val className = klass.name
    if (className != null && className.length > 1) {
      // Convert PascalCase to kebab-case
      val kebab = className
        .replace(Regex("([a-z])([A-Z])"), "$1-$2")
        .replace(Regex("([A-Z])([A-Z][a-z])"), "$1-$2")
        .lowercase()
      if (kebab.contains("-")) {
        LOG.info("Lit DevTools: Derived tag name '${kebab}' from class name '${className}'")
        return kebab
      }
    }
    
    return null
  }

  fun litMembers(klass: TypeScriptClass): Triple<List<LitProp>, List<LitProp>, List<JSField>> {
    val props = mutableListOf<LitProp>()
    val states = mutableListOf<LitProp>()
    val privs = mutableListOf<JSField>()

    val attributeOptionRegex = Regex("@property\\s*\\(\\s*\\{[^}]*attribute\\s*:\\s*['\"]([a-zA-Z0-9_-]+)['\"][^}]*}\\s*\\)")

    klass.fields.forEach { f ->
      val attrList: JSAttributeList? = f.attributeList
      val decorators = attrList?.decorators ?: emptyArray()
      var isProperty = false
      var isState = false
      var attrName: String? = null
      decorators.forEach { d ->
        val t = d.text
        if (t.startsWith("@property")) {
          isProperty = true
          val m = attributeOptionRegex.find(t)
          attrName = m?.groupValues?.get(1) ?: f.name
        }
        if (t.startsWith("@state")) isState = true
      }
      val defaultVal = f.initializer?.text
      val typeStr = f.jsType?.typeText
      if (isProperty) props += LitProp(f.name ?: "", attrName, typeStr, defaultVal, f)
      else if (isState) states += LitProp(f.name ?: "", null, typeStr, defaultVal, f)
      else if (attrList?.accessType == JSAttributeList.AccessType.PRIVATE || f.name?.startsWith("_") == true) privs += f
    }

    // Static properties object pattern: static properties = { foo: { ... }, bar: { ... } }
    klass.fields.firstOrNull { it.name == "properties" }?.let { staticPropsField ->
      val text = staticPropsField.text
      val entryRegex = Regex("([a-zA-Z_][a-zA-Z0-9_]*)\\s*:")
      entryRegex.findAll(text).forEach { m ->
        val name = m.groupValues[1]
        if (props.none { it.name == name }) {
          props += LitProp(name, name, null, null, staticPropsField)
        }
      }
    }
    return Triple(props, states, privs)
  }

  fun methods(klass: TypeScriptClass): List<JSFunction> = klass.functions.toList()

  fun events(klass: TypeScriptClass): List<String> = eventsFromText(klass.text)

  fun eventsFromText(source: String): List<String> {
    val regex = Regex("dispatchEvent\\(\\s*new\\s+CustomEvent\\(\\s*['\"]([a-zA-Z0-9_-]+)['\"]")
    return regex.findAll(source).map { it.groupValues[1] }.distinct().toList()
  }

  fun hasStyles(klass: TypeScriptClass): Boolean =
    klass.fields.any { it.name == "styles" || it.text.contains("css`") }

  fun tryBuildComponent(klass: TypeScriptClass): LitComponent? {
    if (!isLitElement(klass)) return null
    val tag = customElementTag(klass) ?: return null
    val (props, states, privs) = litMembers(klass)
    val component = LitComponent(
      tagName = tag,
      jsClass = klass,
      properties = props,
      states = states,
      privates = privs,
      methods = methods(klass),
      events = events(klass),
      hasStyles = hasStyles(klass)
    )
    LOG.info("Lit DevTools: Found component <${tag}> with ${props.size} properties, ${states.size} state fields, ${component.events.size} events")
    return component
  }
}
