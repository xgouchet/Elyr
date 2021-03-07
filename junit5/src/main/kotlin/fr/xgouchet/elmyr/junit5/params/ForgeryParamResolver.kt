package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.Forgery
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.util.Collection
import java.util.List
import java.util.Set
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext

internal object ForgeryParamResolver :
    ForgeryResolver<Unit> {

    // region ForgeryResolver

    /** @inheritdoc */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: Unit
    ): Boolean {
        return parameterContext.isAnnotated(Forgery::class.java)
    }

    /** @inheritdoc */
    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: Unit,
        forge: Forge
    ): Any? {
        val type = parameterContext.parameter.type
        val parameterizedType = parameterContext.parameter.parameterizedType
        return resolveParameter(parameterizedType ?: type, forge)
    }

    // endregion

    // region Internal

    internal fun resolveParameter(
        type: Type,
        forge: Forge
    ): Any? {
        return when (type) {
            is Class<*> -> forge.getForgery(type)
            is WildcardType -> resolveParameter(type.upperBounds.first(), forge)
            is ParameterizedType -> resolveParameterizedForgery(forge, type.rawType, type.actualTypeArguments)
            else -> null
        }
    }

    private fun resolveParameterizedForgery(
        forge: Forge,
        rawType: Type,
        typeArgs: Array<Type>
    ): Any? {
        return when (rawType) {
            in listClasses -> forge.aList { resolveParameter(typeArgs[0], forge) }
            in setClasses -> forge.aList { resolveParameter(typeArgs[0], forge) }.toSet()
            in mapClasses -> forge.aList {
                val key = resolveParameter(typeArgs[0], forge)
                val value = resolveParameter(typeArgs[1], forge)
                key to value
            }.toMap()
            else -> resolveParameter(rawType, forge)
        }
    }

    // endregion

    private val listClasses = arrayOf(List::class.java, Collection::class.java)
    private val setClasses = arrayOf(Set::class.java)
    private val mapClasses = arrayOf(Map::class.java)
}
