package fr.xgouchet.elmyr.junit5.shrink.params

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.DoubleForgery
import fr.xgouchet.elmyr.junit5.params.DoubleForgeryParamResolver
import fr.xgouchet.elmyr.junit5.shrink.InvocationReport
import fr.xgouchet.elmyr.junit5.shrink.boundaries
import fr.xgouchet.elmyr.junit5.shrink.reportForParam
import org.junit.jupiter.api.extension.ParameterContext

internal class DoubleShrinkingParamResolver :
    DoubleForgeryParamResolver<List<InvocationReport>>() {

    // region PrimitiveForgeryParamResolver

    override fun supportsForgeryContext(forgeryContext: List<InvocationReport>): Boolean {
        return forgeryContext.any { it.exception == null } &&
                forgeryContext.any { it.exception != null }
    }

    override fun forgePrimitive(
        annotation: DoubleForgery,
        parameterContext: ParameterContext,
        forgeryContext: List<InvocationReport>,
        forge: Forge
    ): Any? {
        val paramName = parameterContext.parameter.name
        val values = forgeryContext.reportForParam<Double>(paramName)

        val boundaries = values.boundaries { a, b ->
            ((a + b) / 2)
        }

        return if (boundaries.isEmpty()) {
            super.forgePrimitive(annotation, parameterContext, forgeryContext, forge)
        } else {
            forge.anElementFrom(boundaries)
        }
    }

    // endregion
}
