package com.david.litdevtools.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.lang.javascript.psi.JSLiteralExpression
import com.intellij.lang.javascript.psi.ecma6.ES6TaggedTemplateExpression

/**
 * Injects HTML language into Lit template literals (html`...`)
 * This enables HTML support inside JavaScript/TypeScript template strings
 */
class LitHtmlInjector : MultiHostInjector {
    private val LOG = Logger.getInstance(LitHtmlInjector::class.java)
    
    init {
        LOG.info("Lit DevTools: LitHtmlInjector initialized")
    }
    
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        // We work with tagged template expressions like html`...`
        val taggedTemplate = context as? ES6TaggedTemplateExpression ?: return
        
        // Check if the tag is "html"
        val tag = taggedTemplate.tag
        if (tag == null || tag.text != "html") return
        
        LOG.info("Lit DevTools: Found html tagged template, injecting HTML language")
        
        // Get the template parts
        val template = taggedTemplate.template ?: return
        val strings = template.stringRangesWithoutExpressions
        
        if (strings.isEmpty()) return
        
        registrar.startInjecting(HTMLLanguage.INSTANCE)
        
        // Inject into each string part (between ${...} expressions)
        strings.forEach { range ->
            val host = template as? PsiLanguageInjectionHost ?: return@forEach
            registrar.addPlace(null, null, host, range)
        }
        
        registrar.doneInjecting()
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(ES6TaggedTemplateExpression::class.java)
    }
}
