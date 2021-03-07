package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver

internal interface ForgeryResolver<C> {

    /**
     * Determine if this resolver supports resolution of an argument for the
     * Parameter in the supplied [ParameterContext] for the supplied
     * [ExtensionContext].
     *
     * @param parameterContext the context for the parameter;
     * @param extensionContext the extension context;
     * @return true if this resolver can resolve an argument for the parameter
     * @see ParameterContext
     * @see ParameterResolver
     */
    fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: C
    ): Boolean

    /**
     * Resolve an argument for the {@link Parameter} in the supplied [ParameterContext]
     * for the supplied [ExtensionContext].
     *
     * @param parameterContext the context for the parameter; never {@code null}
     * @param extensionContext the extension context for the {@code Executable}
     * about to be invoked; never {@code null}
     * @param forge the [Forge] to use;
     * @return the resolved argument for the parameter; may only be {@code null} if the
     * parameter type is not a primitive
     * @see #supportsParameter
     * @see ParameterContext
     */
    fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: C,
        forge: Forge
    ): Any?
}
