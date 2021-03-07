package fr.xgouchet.elmyr.junit5.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.IntForgery
import org.junit.jupiter.api.extension.ParameterContext

internal open class IntForgeryParamResolver<C> :
    PrimitiveForgeryParamResolver<IntForgery, C>(
        java.lang.Integer.TYPE,
        java.lang.Integer::class.java,
        IntForgery::class.java
    ) {

    // region PrimitiveForgeryParamResolver

    override fun supportsForgeryContext(forgeryContext: C): Boolean {
        return true
    }

    override fun forgePrimitive(
        annotation: IntForgery,
        parameterContext: ParameterContext,
        forgeryContext: C,
        forge: Forge
    ): Any? {
        return if (annotation.standardDeviation >= 0) {
            check(annotation.min == Int.MIN_VALUE) {
                "You can only use an IntForgery with min and max or with mean and standardDeviation"
            }
            check(annotation.max == Int.MAX_VALUE) {
                "You can only use an IntForgery with min and max or with mean and standardDeviation"
            }
            forge.aGaussianInt(annotation.mean, annotation.standardDeviation)
        } else {
            check(annotation.mean == 0) {
                "You can only use an IntForgery with min and max or with mean and standardDeviation"
            }
            forge.anInt(annotation.min, annotation.max)
        }
    }

    // endregion
}
