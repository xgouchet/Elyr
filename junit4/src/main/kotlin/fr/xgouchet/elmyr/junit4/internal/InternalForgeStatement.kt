package fr.xgouchet.elmyr.junit4.internal

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.junit4.ForgeAnnotationEngine
import fr.xgouchet.elmyr.junit4.internal.engine.CombinedAnnotationEngine
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement
import java.util.Locale

internal class InternalForgeStatement(
    private val base: Statement,
    private val method: FrameworkMethod,
    private val target: Any,
    private val forge: Forge
) : Statement() {

    private val injector: ForgeAnnotationEngine = CombinedAnnotationEngine(forge)

    // region Statement

    @Suppress("TooGenericExceptionCaught")
    override fun evaluate() {
        val errors = mutableListOf<Throwable>()

        performQuietly(errors) { starting() }

        try {
            injector.process(target)
            base.evaluate()
            performQuietly(errors) { succeeded() }
        } catch (e: org.junit.internal.AssumptionViolatedException) {
            errors.add(e)
            performQuietly(errors) { skipped() }
        } catch (e: Throwable) {
            errors.add(e)
            performQuietly(errors) { failed() }
        } finally {
            performQuietly(errors) { finished() }
        }

        MultipleFailureException.assertEmpty(errors)
    }

    // endregion

    // region Internal

    @Suppress("TooGenericExceptionCaught")
    private fun performQuietly(
        errors: MutableList<Throwable>,
        operation: () -> Unit
    ) {
        try {
            operation()
        } catch (e: Throwable) {
            errors.add(e)
        }
    }

    private fun starting() {
        resetSeed()
    }

    @Suppress("EmptyFunctionBlock")
    private fun succeeded() {}

    @Suppress("EmptyFunctionBlock")
    private fun skipped() {}

    private fun failed() {
        val message = "<%s.%s()> failed with Forge seed 0x%x\n" +
                "Add the following line in your @Before method to reproduce :\n\n" +
                "\tforger.resetSeed(0x%xL)\n"
        System.err.println(
                message.format(
                        Locale.US,
                        target.javaClass.simpleName,
                        method.name,
                        forge.seed,
                        forge.seed
                )
        )
    }

    @Suppress("EmptyFunctionBlock")
    private fun finished() {}

    private fun resetSeed() {
        val seed = (System.nanoTime() and SEED_MASK)
        forge.seed = seed
    }

    // endregion

    companion object {
        const val SEED_MASK = 0x7FFFFFFFL
    }
}