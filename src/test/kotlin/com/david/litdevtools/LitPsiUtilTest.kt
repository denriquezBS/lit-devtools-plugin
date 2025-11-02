package com.david.litdevtools

import com.david.litdevtools.psi.LitPsiUtil
import org.junit.Test
import org.junit.Assert.*

class LitPsiUtilTest {
  @Test
  fun testEventsParsing() {
    val txt = """
      class X extends LitElement { foo(){ this.dispatchEvent(new CustomEvent('a')); this.dispatchEvent(new CustomEvent("b")); } }
    """.trimIndent()
    val events = LitPsiUtil.eventsFromText(txt)
    assertTrue(events.contains("a"))
    assertTrue(events.contains("b"))
  }
}
