package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext

internal open class ForgeParamResolver<C> : ForgeryResolver<C> {

    /** @inheritdoc */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: C
    ): Boolean {
        return parameterContext.parameter.type == Forge::class.java
    }

    /** @inheritdoc */
    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: C,
        forge: Forge
    ): Any? {
        return forge
    }
}
