package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType
import org.junit.jupiter.api.extension.ParameterContext

internal open class StringForgeryParamResolver<C> :
    PrimitiveForgeryParamResolver<StringForgery, C>(
        null,
        java.lang.String::class.java,
        StringForgery::class.java
    ) {

    // region PrimitiveForgeryParamResolver

    override fun supportsForgeryContext(forgeryContext: C): Boolean {
        return true
    }

    override fun forgePrimitive(
        annotation: StringForgery,
        parameterContext: ParameterContext,
        forgeryContext: C,
        forge: Forge
    ): Any? {
        return if (annotation.regex.isNotEmpty()) {
            forge.aStringMatching(annotation.regex)
        } else when (annotation.type) {
            StringForgeryType.ALPHABETICAL -> forge.anAlphabeticalString(
                annotation.case,
                annotation.size
            )
            StringForgeryType.ALPHA_NUMERICAL -> forge.anAlphaNumericalString(
                annotation.case,
                annotation.size
            )
            StringForgeryType.NUMERICAL -> forge.aNumericalString(annotation.size)
            StringForgeryType.HEXADECIMAL -> forge.anHexadecimalString(
                annotation.case,
                annotation.size
            )
            StringForgeryType.WHITESPACE -> forge.aWhitespaceString(annotation.size)
            StringForgeryType.ASCII -> forge.anAsciiString(annotation.size)
            StringForgeryType.ASCII_EXTENDED -> forge.anExtendedAsciiString(annotation.size)
        }
    }

    // endregion
}
