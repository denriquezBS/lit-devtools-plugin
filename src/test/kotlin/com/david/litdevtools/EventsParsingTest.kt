package com.david.litdevtools

import com.david.litdevtools.psi.LitPsiUtil
import org.junit.Test
import org.junit.Assert.*

class EventsParsingTest {
  @Test
  fun extractsDistinctEvents() {
    val src = """
      class A extends LitElement { connectedCallback(){ this.dispatchEvent(new CustomEvent('open')); this.dispatchEvent(new CustomEvent("close")); this.dispatchEvent(new CustomEvent('open')); } }
    """.trimIndent()
    val events = LitPsiUtil.eventsFromText(src)
    assertEquals(setOf("open","close"), events.toSet())
  }
}

