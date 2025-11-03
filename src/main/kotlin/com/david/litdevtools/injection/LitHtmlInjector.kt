package com.david.litdevtools.injection

import com.intellij.lang.injection.MultiHostInjector
import com.intellij.lang.injection.MultiHostRegistrar
import com.intellij.lang.html.HTMLLanguage
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.lang.javascript.psi.ecma6.ES6TaggedTemplateExpression
import com.intellij.lang.javascript.psi.JSEmbeddedContent

/**
 * Injects HTML language into Lit template literals (html`...`)
 * This enables HTML support inside JavaScript/TypeScript template strings,
 * which in turn enables our XML-based completion and navigation contributors to work.
 */
class LitHtmlInjector : MultiHostInjector {
    private val LOG = Logger.getInstance(LitHtmlInjector::class.java)
    
    init {
        LOG.info("Lit DevTools: LitHtmlInjector initialized")
    }
    
    override fun getLanguagesToInject(registrar: MultiHostRegistrar, context: PsiElement) {
        // We work with tagged template expressions like html`...`
        val taggedTemplate = context as? ES6TaggedTemplateExpression ?: return
        
        // Check if the tag is "html" or "css"
        val tag = taggedTemplate.tag
        if (tag == null) return
        
        val tagText = tag.text
        if (tagText != "html") return
        
        LOG.info("Lit DevTools: Found html tagged template, injecting HTML language")
        
        // Get the template parts
        val template = taggedTemplate.template ?: return
        
        // Get the embedded content (the parts between backticks)
        val content = template.children.filterIsInstance<JSEmbeddedContent>().firstOrNull() ?: return
        
        if (content !is PsiLanguageInjectionHost) {
            LOG.warn("Lit DevTools: Template content is not a valid injection host")
            return
        }
        
        // Inject HTML into the template content
        registrar.startInjecting(HTMLLanguage.INSTANCE)
        
        // Inject into the entire content
        // The content already handles splitting around ${...} expressions
        val textRange = TextRange(0, content.textLength)
        registrar.addPlace(null, null, content, textRange)
        
        registrar.doneInjecting()
        
        LOG.info("Lit DevTools: Successfully injected HTML language into template literal")
    }

    override fun elementsToInjectIn(): List<Class<out PsiElement>> {
        return listOf(ES6TaggedTemplateExpression::class.java)
    }
}
