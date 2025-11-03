package com.david.litdevtools.completion

import com.david.litdevtools.psi.LitPsiUtil
import com.david.litdevtools.index.LitTagResolver
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.lang.javascript.psi.ecma6.ES6TaggedTemplateExpression
import com.intellij.lang.javascript.psi.JSFile
import com.intellij.openapi.diagnostic.Logger
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.FilenameIndex
import com.intellij.util.ProcessingContext

/**
 * Provides completion for Lit component properties and events in template literals.
 * Works inside html`...` template expressions.
 */
class LitTemplateCompletionContributor : CompletionContributor() {
    private val LOG = Logger.getInstance(LitTemplateCompletionContributor::class.java)
    
    init {
        LOG.info("Lit DevTools: LitTemplateCompletionContributor initialized")
        
        // Match any element - we'll filter in the provider
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            object : CompletionProvider<CompletionParameters>() {
                override fun addCompletions(
                    parameters: CompletionParameters,
                    context: ProcessingContext,
                    result: CompletionResultSet
                ) {
                    val position = parameters.position
                    
                    // Check if we're inside an html`...` template
                    if (!isInsideHtmlTemplate(position)) {
                        return
                    }
                    
                    LOG.info("Lit DevTools: Attempting completion in html template literal")
                    
                    // Try to find the tag name we're completing for
                    val tagName = findTagName(position) ?: return
                    
                    LOG.info("Lit DevTools: Attempting completion for tag <${tagName}>")
                    
                    val klass = resolveTagToClass(tagName, position.project) ?: run {
                        LOG.info("Lit DevTools: No Lit component found for tag <${tagName}>")
                        return
                    }
                    
                    val comp = LitPsiUtil.tryBuildComponent(klass) ?: return
                    
                    LOG.info("Lit DevTools: Providing ${comp.properties.size} property completions and ${comp.events.size} event completions for <${tagName}>")
                    
                    // Add property completions with custom icon
                    comp.properties.forEach { prop ->
                        val le = LookupElementBuilder.create(prop.attrName ?: prop.name)
                            .withTypeText(prop.jsType ?: "any", true)
                            .withTailText(prop.defaultValue?.let { " = $it" } ?: "", true)
                            .withIcon(AllIcons.Nodes.Property)
                        result.addElement(le)
                    }
                    
                    // Add event completions with custom icon
                    comp.events.forEach { ev ->
                        val le = LookupElementBuilder.create("@${ev}")
                            .withTypeText("event", true)
                            .withIcon(AllIcons.Nodes.Method)
                        result.addElement(le)
                    }
                }
            }
        )
    }
    
    private fun isInsideHtmlTemplate(element: com.intellij.psi.PsiElement): Boolean {
        var current: com.intellij.psi.PsiElement? = element
        while (current != null) {
            if (current is ES6TaggedTemplateExpression) {
                val tag = current.tag
                return tag?.text == "html"
            }
            current = current.parent
        }
        return false
    }
    
    private fun findTagName(element: com.intellij.psi.PsiElement): String? {
        // Look for pattern like <tag-name attr=|
        val text = element.containingFile.text
        val offset = element.textRange.startOffset
        
        // Simple backward search for the opening tag
        var i = offset - 1
        while (i >= 0 && text[i] != '<' && text[i] != '>') {
            i--
        }
        
        if (i < 0 || text[i] != '<') return null
        
        // Extract tag name
        val tagStart = i + 1
        var tagEnd = tagStart
        while (tagEnd < text.length && text[tagEnd].let { it.isLetterOrDigit() || it == '-' }) {
            tagEnd++
        }
        
        val tagName = text.substring(tagStart, tagEnd)
        return if (tagName.contains("-")) tagName else null
    }
    
    private fun resolveTagToClass(
        tagName: String,
        project: com.intellij.openapi.project.Project
    ): com.intellij.lang.javascript.psi.ecma6.TypeScriptClass? {
        val scope = GlobalSearchScope.projectScope(project)
        val psiManager = PsiManager.getInstance(project)
        
        listOf("ts", "js", "tsx", "jsx", "mjs").forEach { ext ->
            FilenameIndex.getAllFilesByExt(project, ext, scope).forEach { vf ->
                val psiFile = psiManager.findFile(vf) as? JSFile ?: return@forEach
                val components = LitTagResolver.findCandidates(psiFile)
                components[tagName]?.let { return it }
            }
        }
        return null
    }
}
