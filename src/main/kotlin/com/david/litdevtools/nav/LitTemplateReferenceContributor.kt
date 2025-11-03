package com.david.litdevtools.nav

import com.david.litdevtools.index.LitTagResolver
import com.intellij.lang.javascript.psi.ecma6.ES6TaggedTemplateExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.*
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.util.elementType
import com.intellij.lang.javascript.JSTokenTypes

/**
 * Provides navigation from custom element tags inside Lit html templates to their class definitions.
 * Works with both HTML/XML files and TypeScript/JavaScript template literals.
 */
class LitTemplateReferenceContributor : PsiReferenceContributor() {
    private val LOG = Logger.getInstance(LitTemplateReferenceContributor::class.java)
    
    init {
        LOG.info("Lit DevTools: LitTemplateReferenceContributor initialized")
    }
    
    override fun registerReferenceProviders(registrar: PsiReferenceRegistrar) {
        LOG.info("Lit DevTools: Registering template reference providers")
        
        // Register for any PSI element - we'll filter in the provider
        registrar.registerReferenceProvider(
            com.intellij.patterns.PlatformPatterns.psiElement(),
            object : PsiReferenceProvider() {
                override fun getReferencesByElement(element: PsiElement, context: com.intellij.util.ProcessingContext): Array<PsiReference> {
                    // Only process XML_NAME tokens that look like custom elements (contain hyphen)
                    if (element.elementType != com.intellij.psi.xml.XmlTokenType.XML_NAME) {
                        // Also check for text in template literals
                        if (element.elementType != JSTokenTypes.IDENTIFIER && 
                            element.elementType != JSTokenTypes.STRING_LITERAL &&
                            element.text.contains("-").not()) {
                            return PsiReference.EMPTY_ARRAY
                        }
                    }
                    
                    val text = element.text
                    
                    // Check if this looks like a custom element (has hyphen)
                    if (!text.contains("-")) {
                        return PsiReference.EMPTY_ARRAY
                    }
                    
                    // Check if we're in an html`...` template literal
                    val isInHtmlTemplate = isInsideHtmlTemplate(element)
                    val isXmlTag = element.parent is com.intellij.psi.xml.XmlTag || 
                                  element.parent is com.intellij.psi.xml.XmlAttribute
                    
                    if (!isInHtmlTemplate && !isXmlTag) {
                        return PsiReference.EMPTY_ARRAY
                    }
                    
                    val tagName = text.trim('<', '>', '/', '"', '\'')
                    
                    LOG.info("Lit DevTools: Looking up navigation reference for tag <${tagName}>")
                    
                    val project = element.project
                    val scope = GlobalSearchScope.projectScope(project)
                    val psiManager = PsiManager.getInstance(project)
                    val candidates = mutableListOf<PsiElement>()
                    
                    // Search for the component definition
                    listOf("ts", "js", "tsx", "jsx", "mjs").forEach { ext ->
                        FilenameIndex.getAllFilesByExt(project, ext, scope).forEach { vf ->
                            val psiFile = psiManager.findFile(vf) as? JSFile ?: return@forEach
                            val components = LitTagResolver.findCandidates(psiFile)
                            components[tagName]?.let { candidates.add(it) }
                        }
                    }
                    
                    if (candidates.isNotEmpty()) {
                        LOG.info("Lit DevTools: Found ${candidates.size} navigation target(s) for <${tagName}>")
                        return arrayOf(object : PsiReferenceBase<PsiElement>(element, true) {
                            override fun resolve(): PsiElement? = candidates.firstOrNull()
                            override fun getVariants(): Array<Any> = emptyArray()
                        })
                    }
                    
                    return PsiReference.EMPTY_ARRAY
                }
            }
        )
    }
    
    private fun isInsideHtmlTemplate(element: PsiElement): Boolean {
        var current: PsiElement? = element
        while (current != null) {
            if (current is ES6TaggedTemplateExpression) {
                val tag = current.tag
                return tag?.text == "html"
            }
            current = current.parent
        }
        return false
    }
}
