package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.AdvancedForgery
import java.lang.reflect.Type
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext

internal object AdvancedForgeryParamResolver : ForgeryResolver<Unit> {

    // region ForgeryResolver

    /** @inheritdoc */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: Unit
    ): Boolean {
        return parameterContext.isAnnotated(AdvancedForgery::class.java)
    }

    /** @inheritdoc */
    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: Unit,
        forge: Forge
    ): Any? {
        val annotation = parameterContext.findAnnotation(AdvancedForgery::class.java).get()
        val type = parameterContext.parameter.parameterizedType
        return resolveAdvancedParameter(annotation, parameterContext, type, forge)
    }

    // endregion

    // region Internal

    internal fun resolveAdvancedParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Any? {
        return when {
            annotation.string.isNotEmpty() -> resolveStringParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            annotation.int.isNotEmpty() -> resolveIntParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            annotation.long.isNotEmpty() -> resolveLongParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            annotation.float.isNotEmpty() -> resolveFloatParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            annotation.double.isNotEmpty() -> resolveDoubleParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            annotation.map.isNotEmpty() -> resolveMapParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            annotation.pair.isNotEmpty() -> resolvePairParameter(
                annotation,
                parameterContext,
                type,
                forge
            )
            else -> ForgeryParamResolver.resolveParameter(type, forge)
        }
    }

    private fun resolveDoubleParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Any? {
        val usingAnnotation = forge.anElementFrom(*annotation.double)
        return DoubleForgeryParamResolver<Unit>().resolveParameter(
            usingAnnotation,
            parameterContext,
            Unit,
            type,
            forge
        )
    }

    private fun resolveFloatParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Any? {
        val usingAnnotation = forge.anElementFrom(*annotation.float)
        return FloatForgeryParamResolver<Unit>().resolveParameter(
            usingAnnotation,
            parameterContext,
            Unit,
            type,
            forge
        )
    }

    private fun resolveLongParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Any? {
        val usingAnnotation = forge.anElementFrom(*annotation.long)
        return LongForgeryParamResolver<Unit>().resolveParameter(
            usingAnnotation,
            parameterContext,
            Unit,
            type,
            forge
        )
    }

    private fun resolveIntParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Any? {
        val usingAnnotation = forge.anElementFrom(*annotation.int)
        return IntForgeryParamResolver<Unit>().resolveParameter(
            usingAnnotation,
            parameterContext,
            Unit,
            type,
            forge
        )
    }

    private fun resolveStringParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Any? {
        val usingAnnotation = forge.anElementFrom(*annotation.string)
        return StringForgeryParamResolver<Unit>().resolveParameter(
            usingAnnotation,
            parameterContext,
            Unit,
            type,
            forge
        )
    }

    private fun resolveMapParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Map<Any?, Any?> {
        val usingAnnotation = forge.anElementFrom(*annotation.map)
        return MapForgeryParamResolver.resolveMapParameter(
            usingAnnotation,
            parameterContext,
            type,
            forge
        )
    }

    private fun resolvePairParameter(
        annotation: AdvancedForgery,
        parameterContext: ParameterContext,
        type: Type,
        forge: Forge
    ): Pair<Any?, Any?> {
        val usingAnnotation = forge.anElementFrom(*annotation.pair)
        return PairForgeryParamResolver.resolvePairParameter(
            usingAnnotation,
            parameterContext,
            type,
            forge
        )
    }

    // endregion
}
