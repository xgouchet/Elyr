package fr.xgouchet.elmyr.junit5.shrink

import org.junit.jupiter.api.extension.TestTemplateInvocationContext

internal class ShrinkInvocationContext(
    private val baseName: String
) : TestTemplateInvocationContext {

    override fun getDisplayName(invocationIndex: Int): String {
        return "$baseName [$invocationIndex]"
    }
}
