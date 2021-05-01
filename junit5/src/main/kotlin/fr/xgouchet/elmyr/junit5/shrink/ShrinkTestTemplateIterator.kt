package fr.xgouchet.elmyr.junit5.shrink

import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

internal class ShrinkTestTemplateIterator(
    private val testContext: ExtensionContext,
    maxRunCount: Int
) : Iterator<TestTemplateInvocationContext> {

    private var index = 0
    private val intRange = 0 until maxRunCount

    // region Iterator

    override fun hasNext(): Boolean {
        if (index !in intRange) {
            return false
        }
        return true
    }

    override fun next(): TestTemplateInvocationContext {
        if (index !in intRange) {
            throw NoSuchElementException()
        }
        index++
        return ShrinkInvocationContext(testContext.displayName)
    }

    // endregion
}
