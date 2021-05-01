package fr.xgouchet.elmyr.junit5.shrink.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.junit5.params.StringForgeryParamResolver
import fr.xgouchet.elmyr.junit5.shrink.InvocationReport
import fr.xgouchet.elmyr.junit5.shrink.reportForParam
import org.junit.jupiter.api.extension.ParameterContext

internal class StringShrinkingParamResolver :
    StringForgeryParamResolver<List<InvocationReport>>() {

    // region PrimitiveForgeryParamResolver

    override fun supportsForgeryContext(forgeryContext: List<InvocationReport>): Boolean {
        return forgeryContext.any { it.exception == null } &&
                forgeryContext.any { it.exception != null }
    }

    override fun forgePrimitive(
        annotation: StringForgery,
        parameterContext: ParameterContext,
        forgeryContext: List<InvocationReport>,
        forge: Forge
    ): Any? {
        val paramName = parameterContext.parameter.name
        val values = forgeryContext.reportForParam<String>(paramName)

        val testedValues = values.map { it.target.value }
        val shrunkValues = mutableListOf<String>()
        values.filter { it.exception != null }
            .forEach {
                val shrunk = shrink(it.target.value, annotation, forge)
                if (shrunk != null && shrunk !in testedValues) {
                    shrunkValues.add(shrunk)
                }
            }

        return if (shrunkValues.isEmpty()) {
            super.forgePrimitive(annotation, parameterContext, forgeryContext, forge)
        } else {
            forge.anElementFrom(shrunkValues)
        }
    }

    // endregion

    // region Internal


    private fun shrink(value: String, annotation: StringForgery, forge: Forge): String? {
        return if (annotation.regex.isNotEmpty()) {
            null
        } else {
            val length = value.length
            when (length) {
                0 -> null
                1 -> ""
                else -> forge.aSubStringOf(value, length / 2)
            }
        }
    }

    // endregion
}