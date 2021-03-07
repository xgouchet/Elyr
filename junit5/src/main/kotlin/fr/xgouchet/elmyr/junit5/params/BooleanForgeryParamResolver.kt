package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.BoolForgery
import org.junit.jupiter.api.extension.ParameterContext

internal open class BooleanForgeryParamResolver<C> :
    PrimitiveForgeryParamResolver<BoolForgery, C>(
        java.lang.Boolean.TYPE,
        java.lang.Boolean::class.java,
        BoolForgery::class.java
    ) {

    // region PrimitiveForgeryParamResolver

    override fun supportsForgeryContext(forgeryContext: C): Boolean {
        return true
    }

    override fun forgePrimitive(
        annotation: BoolForgery,
        parameterContext: ParameterContext,
        forgeryContext: C,
        forge: Forge
    ): Any? {
        check(annotation.probability >= 0f) {
            "You can only use an BoolForgery with a probability between 0f and 1f"
        }
        check(annotation.probability <= 1f) {
            "You can only use an BoolForgery with a probability between 0f and 1f"
        }
        return forge.aBool(annotation.probability)
    }

    // endregion
}
