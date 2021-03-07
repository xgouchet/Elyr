package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.FloatForgery
import org.junit.jupiter.api.extension.ParameterContext

internal open class FloatForgeryParamResolver<C> :
    PrimitiveForgeryParamResolver<FloatForgery, C>(
        java.lang.Float.TYPE,
        java.lang.Float::class.java,
        FloatForgery::class.java
    ) {

    // region PrimitiveForgeryParamResolver

    override fun supportsForgeryContext(forgeryContext: C): Boolean {
        return true
    }

    override fun forgePrimitive(
        annotation: FloatForgery,
        parameterContext: ParameterContext,
        forgeryContext: C,
        forge: Forge
    ): Any? {
        return if (!annotation.standardDeviation.isNaN()) {
            check(annotation.min == -Float.MAX_VALUE) {
                "You can only use an FloatForgery with min and max or with mean and standardDeviation"
            }
            check(annotation.max == Float.MAX_VALUE) {
                "You can only use an FloatForgery with min and max or with mean and standardDeviation"
            }
            forge.aGaussianFloat(annotation.mean, annotation.standardDeviation)
        } else {
            check(annotation.mean == 0f) {
                "You can only use an FloatForgery with min and max or with mean and standardDeviation"
            }
            forge.aFloat(annotation.min, annotation.max)
        }
    }

    // endregion
}
