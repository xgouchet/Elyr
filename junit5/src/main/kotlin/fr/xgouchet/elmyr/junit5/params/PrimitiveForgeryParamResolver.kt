package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import java.lang.IllegalStateException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext

internal abstract class PrimitiveForgeryParamResolver<A : Annotation, C>(
    private val primitiveClass: Class<*>?,
    private val primitiveBoxingClass: Class<*>,
    private val annotationClass: Class<A>
) : ForgeryResolver<C> {

    // region ForgeryResolver

    /** @inheritdoc */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: C
    ): Boolean {
        val annotated = parameterContext.isAnnotated(annotationClass)
        return if (annotated) {
            check(supportsParameter(parameterContext.parameter.parameterizedType)) {
                if (primitiveClass == null) {
                    "@${annotationClass.simpleName} can only be used on a Java or a Kotlin " +
                            "${primitiveBoxingClass.simpleName}, or a List, Set or Collection of on of those classes."
                } else {
                    "@${annotationClass.simpleName} can only be used on a Java ${primitiveClass.simpleName} or " +
                            "${primitiveBoxingClass.simpleName}, a Kotlin ${primitiveBoxingClass.simpleName}, " +
                            "or a List, Set or Collection of on of those classes."
                }
            }
            supportsForgeryContext(forgeryContext)
        } else {
            false
        }
    }

    /** @inheritdoc */
    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext,
        forgeryContext: C,
        forge: Forge
    ): Any? {
        val annotation = parameterContext.findAnnotation(annotationClass).get()
        return resolveParameter(
            annotation,
            parameterContext,
            forgeryContext,
            parameterContext.parameter.parameterizedType,
            forge
        )
    }

    // endregion

    // region PrimitiveForgeryParamResolver

    protected abstract fun supportsForgeryContext(forgeryContext: C): Boolean

    protected abstract fun forgePrimitive(
        annotation: A,
        parameterContext: ParameterContext,
        forgeryContext: C,
        forge: Forge
    ): Any?

    // endregion

    // region Internal

    private fun supportsParameter(type: Type): Boolean {
        if (type == primitiveClass || type == primitiveBoxingClass) return true

        if (type is WildcardType) return type.upperBounds.any { supportsParameter(it) }

        if (type is ParameterizedType && type.rawType in collectionTypes) {
            val typeParam = type.actualTypeArguments
            return supportsParameter(typeParam.first())
        }
        return false
    }

    internal fun resolveParameter(
        annotation: A,
        parameterContext: ParameterContext,
        forgeryContext: C,
        type: Type,
        forge: Forge
    ): Any? {
        if (type == primitiveClass || type == primitiveBoxingClass) {
            return forgePrimitive(annotation, parameterContext, forgeryContext, forge)
        }

        if (type is WildcardType) {
            val actualType = type.upperBounds.first { supportsParameter(it) }
            return resolveParameter(annotation, parameterContext, forgeryContext, actualType, forge)
        }

        check(type is ParameterizedType) { "Unable to forge a value with type $type" }
        val typeParam = type.actualTypeArguments
        return when (type.rawType) {
            List::class.java,
            Collection::class.java -> forge.aList {
                resolveParameter(
                    annotation,
                    parameterContext,
                    forgeryContext,
                    typeParam.first(),
                    forge
                )
            }
            Set::class.java -> forge.aList {
                resolveParameter(
                    annotation,
                    parameterContext,
                    forgeryContext,
                    typeParam.first(),
                    forge
                )
            }.toSet()
            else -> throw IllegalStateException("Unable to forge a value with type $type")
        }
    }

    // endregion

    companion object {
        private val collectionTypes =
            arrayOf(List::class.java, Collection::class.java, Set::class.java)
    }
}
