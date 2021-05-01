package fr.xgouchet.elmyr.junit5

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeConfigurator
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.inject.DefaultForgeryInjector
import fr.xgouchet.elmyr.inject.ForgeryInjector
import fr.xgouchet.elmyr.junit5.params.AdvancedForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.BooleanForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.DoubleForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.FloatForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.ForgeParamResolver
import fr.xgouchet.elmyr.junit5.params.ForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.ForgeryResolver
import fr.xgouchet.elmyr.junit5.params.IntForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.LongForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.MapForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.PairForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.StringForgeryParamResolver
import fr.xgouchet.elmyr.junit5.shrink.InjectionReport
import fr.xgouchet.elmyr.junit5.shrink.InvocationReport
import fr.xgouchet.elmyr.junit5.shrink.Shrink
import fr.xgouchet.elmyr.junit5.shrink.ShrinkInvocationContext
import fr.xgouchet.elmyr.junit5.shrink.ShrinkTestTemplateIterator
import fr.xgouchet.elmyr.junit5.shrink.params.DoubleShrinkingParamResolver
import fr.xgouchet.elmyr.junit5.shrink.params.FloatShrinkingParamResolver
import fr.xgouchet.elmyr.junit5.shrink.params.IntShrinkingParamResolver
import fr.xgouchet.elmyr.junit5.shrink.params.LongShrinkingParamResolver
import fr.xgouchet.elmyr.junit5.shrink.params.StringShrinkingParamResolver
import fr.xgouchet.elmyr.junit5.shrink.reportForParam
import java.lang.reflect.Constructor
import java.lang.reflect.Type
import java.util.Locale
import java.util.Spliterator
import java.util.Spliterators.spliteratorUnknownSize
import java.util.stream.Stream
import java.util.stream.StreamSupport.stream
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler
import org.junit.jupiter.api.extension.TestTemplateInvocationContext
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider
import org.junit.platform.commons.support.AnnotationSupport.isAnnotated
import org.junit.platform.commons.util.AnnotationUtils.findAnnotation
import java.lang.AssertionError

/**
 * A JUnit Jupiter extension that can inject forgeries in the test class's fields/properties/method
 * parameters (when annotated with [Forgery]). It can also inject a [Forge] instance in test methods
 * without the need to annotate it.
 *
 * @see Forgery
 * @see Forge
 */
class ForgeExtension :
    BeforeAllCallback,
    BeforeEachCallback,
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback,
    TestExecutionExceptionHandler,
    TestTemplateInvocationContextProvider,
    ParameterResolver,
    ForgeryInjector.Listener {

    internal val instanceForge: Forge = Forge()
    private val injector: ForgeryInjector = DefaultForgeryInjector()

    private val injectedData: MutableList<ForgeTarget<*>> = mutableListOf()

    private val invocationReports = mutableMapOf<String, MutableList<InvocationReport>>()

    private val shrinkingResolvers = listOf<ForgeryResolver<List<InvocationReport>>>(
        IntShrinkingParamResolver(),
        LongShrinkingParamResolver(),
        FloatShrinkingParamResolver(),
        DoubleShrinkingParamResolver(),
        StringShrinkingParamResolver()
    )

    private val parameterResolvers = listOf(
        ForgeParamResolver(),
        BooleanForgeryParamResolver(),
        IntForgeryParamResolver(),
        LongForgeryParamResolver(),
        FloatForgeryParamResolver(),
        DoubleForgeryParamResolver(),
        StringForgeryParamResolver(),
        ForgeryParamResolver,
        AdvancedForgeryParamResolver,
        MapForgeryParamResolver,
        PairForgeryParamResolver
    )

    // region BeforeAllCallback

    /** @inheritdoc */
    override fun beforeAll(context: ExtensionContext) {
        val configurators = getConfigurators(context)

        configurators.forEach {
            it.configure(instanceForge)
        }

        val globalStore = context.getStore(ExtensionContext.Namespace.GLOBAL)
        globalStore.put(EXTENSION_STORE_FORGE_KEY, instanceForge)
    }

    // endregion

    // region BeforeEachCallback

    /** @inheritdoc */
    override fun beforeEach(context: ExtensionContext) {
        resetSeed(context)
        injectedData.clear()
        val target = context.requiredTestInstance
        injector.inject(instanceForge, target, this)
    }

    // endregion

    // region BeforeTestExecutionCallback

    /** @inheritdoc */
    override fun beforeTestExecution(context: ExtensionContext) {
    }

    // endregion

    // region AfterTestExecutionCallback

    override fun afterTestExecution(context: ExtensionContext) {
        if (addShrinkingReport(context, null)) {
            reportLastShrinkingExecution(context)
        }
    }

    // endregion

    // region TestTemplateInvocationContextProvider

    /** @inheritdoc */
    override fun supportsTestTemplate(context: ExtensionContext): Boolean {
        return isAnnotated(context.testMethod, Shrink::class.java)
    }

    /** @inheritdoc */
    override fun provideTestTemplateInvocationContexts(context: ExtensionContext): Stream<TestTemplateInvocationContext> {
        val testMethod = context.testMethod?.orNull()
        requireNotNull(testMethod) { "Test method must not be null" }

        val shrink = findAnnotation<Shrink>(context.testMethod, Shrink::class.java).orNull()
        checkNotNull(shrink) { "Test method must be annotated with @Shrink" }
        check(shrink.maximumRunCount > 0) {
            "@Shrink annotation must have a maximumRunCount greater than 0"
        }

        val isShrinkingEnabled = true
        // TODO System.getProperty("shrinking").orEmpty().toBoolean()
        if (isShrinkingEnabled) {
            val uniqueId = context.uniqueId
            context.getStore(STORE_NAMESPACE).put(STORE_SHRINK_KEY, shrink.maximumRunCount)
            val spliterator = spliteratorUnknownSize(
                ShrinkTestTemplateIterator(
                    context,
                    shrink.maximumRunCount
                ),
                Spliterator.NONNULL
            )
            invocationReports[uniqueId] = mutableListOf()
            return stream(spliterator, false)
        } else {
            System.err.println("Shrinking is not enabled")
            return Stream.of(ShrinkInvocationContext(context.displayName))
        }

    }

    // endregion

    // region TestExecutionExceptionHandler

    /** @inheritdoc */
    override fun handleTestExecutionException(context: ExtensionContext, throwable: Throwable) {
        if (addShrinkingReport(context, throwable)) {
            reportLastShrinkingExecution(context)
        } else {
            System.err.println(getErrorReport(context))
            throw throwable
        }
    }

    private fun getErrorReport(context: ExtensionContext): String {
        val configuration = getConfigurations(context).firstOrNull()
        val errorMessage = getErrorMessage(context)

        val injectedMessage = if (injectedData.isEmpty()) "" else {
            injectedData.joinToString(
                separator = "\n",
                prefix = " and:\n",
                postfix = "\n"
            ) { "\t- ${it.type} ${it.parent}::${it.name} = ${it.value}" }
        }

        val helpMessage = if (configuration == null) {
            "\nAdd the following @ForgeConfiguration annotation to your test class :\n\n" +
                    "\t@ForgeConfiguration(seed = 0x%xL)\n".format(
                        Locale.US,
                        instanceForge.seed
                    )
        } else {
            "\nAdd the seed in your @ForgeConfiguration annotation :\n\n" +
                    "\t@ForgeConfiguration(value = %s::class, seed = 0x%xL)\n".format(
                        Locale.US,
                        configuration.value.simpleName,
                        instanceForge.seed
                    )
        }
        return errorMessage + injectedMessage + helpMessage
    }

    private fun getErrorMessage(context: ExtensionContext) =
        "<%s.%s()> failed with Forge seed 0x%xL".format(
            Locale.US,
            context.requiredTestInstance.javaClass.simpleName,
            context.requiredTestMethod.name,
            instanceForge.seed
        )

    // endregion

    // region ParameterResolver

    /** @inheritdoc */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean {
        val isSupported = parameterResolvers.any {
            it.supportsParameter(parameterContext, extensionContext, Unit)
        }

        if (isSupported && parameterContext.declaringExecutable is Constructor<*>) {
            throw IllegalStateException(
                "@Forgery is not supported on constructor parameters. " +
                        "Please use field injection instead."
            )
        }

        return isSupported
    }

    /** @inheritdoc */
    override fun resolveParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any? {
        return resolveShrinkingParameter(parameterContext, extensionContext)
    }

    private fun resolveShrinkingParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any? {
        val parentUniqueId = extensionContext.parent.orNull()?.uniqueId
        val previousReports = invocationReports[parentUniqueId]
        if (previousReports == null || previousReports.isEmpty()) {
            return resolveRandomParameter(parameterContext, extensionContext)
        }

        val shrinkingResolver = shrinkingResolvers.firstOrNull {
            it.supportsParameter(parameterContext, extensionContext, previousReports)
        }

        if (shrinkingResolver == null) {
            return resolveRandomParameter(parameterContext, extensionContext)
        }

        val value = shrinkingResolver.resolveParameter(
            parameterContext,
            extensionContext,
            previousReports,
            instanceForge
        )
        val target = ForgeTarget.ForgeParamTarget(
            parameterContext.parameter.declaringExecutable.name,
            parameterContext.parameter.name,
            value
        )
        injectedData.add(target)
        return value
    }

    private fun resolveRandomParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Any? {
        val parameterResolver = parameterResolvers.firstOrNull {
            it.supportsParameter(parameterContext, extensionContext, Unit)
        }
        if (parameterResolver == null) {
            println("No resolver found for parameter ${parameterContext.parameter.name}")
            return null
        }

        val value = parameterResolver.resolveParameter(
            parameterContext,
            extensionContext,
            Unit,
            instanceForge
        )
        val target = ForgeTarget.ForgeParamTarget(
            parameterContext.parameter.declaringExecutable.name,
            parameterContext.parameter.name,
            value
        )
        injectedData.add(target)

        return value
    }

    // endregion

    // region ForgeryInjector.Listener

    /** @inheritdoc */
    override fun onFieldInjected(
        declaringClass: Class<*>,
        fieldType: Type,
        fieldName: String,
        value: Any?
    ) {
        injectedData.add(
            ForgeTarget.ForgeFieldTarget(
                declaringClass.simpleName,
                fieldName,
                value
            )
        )
    }

    // endregion

    // region Internal Shrinking

    private fun addShrinkingReport(context: ExtensionContext, throwable: Throwable?): Boolean {
        val parent = context.parent.orNull() ?: return false
        val parentUniqueId = parent.uniqueId
        val report = invocationReports[parentUniqueId] ?: return false
        if (!report.any { it.contextUniqueId == context.uniqueId }) {
            report.add(
                InvocationReport(
                    context.uniqueId,
                    context.displayName,
                    ArrayList(injectedData),
                    throwable
                )
            )
        }
        return true
    }

    private fun reportLastShrinkingExecution(context: ExtensionContext) {
        val parent = context.parent.orNull() ?: return
        val shrinkingCount = parent.getStore(STORE_NAMESPACE).get(STORE_SHRINK_KEY) as? Int
        if (shrinkingCount == null) {
            return
        }

        val parentUniqueId = parent.uniqueId
        val report = invocationReports[parentUniqueId] ?: return
        if (report.size == shrinkingCount) {
            if (report.any { it.exception != null }) {
                reportFailures(parent.displayName, report)
            }
        }
    }

    private fun reportFailures(displayName: String, invocationReports: List<InvocationReport>) {

        val paramTargets = invocationReports.first().injectedData.filter { it.type == "param" }

        val failingMessages = mutableListOf<String>()
        failingMessages.addAll(
            failingRangesReports<Int>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingRangesReports<Long>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingRangesReports<Float>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingRangesReports<Double>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingValuesReports<String>(paramTargets, invocationReports, Comparator { s1, s2 ->
                if (s1.length == s2.length) {
                    s1.compareTo(s2)
                } else {
                    s1.length - s2.length
                }
            })
        )

        throw AssertionError(
            "Test $displayName failed with shrunk param\n" + failingMessages.joinToString("\n"),
            invocationReports.mapNotNull { it.exception }.first()
        )
    }

    private inline fun <reified T : Comparable<T>> failingRangesReports(
        targets: List<ForgeTarget<*>>,
        invocationReports: List<InvocationReport>,
        noinline rangeFactory: (T, T) -> ClosedRange<T>
    ): List<String> {
        val filter = targets.filter { it.value is T }
        return filter
            .map {
                val sortedReports = invocationReports.reportForParam<T>(it.name)
                    .sortedBy { r -> r.target.value }
                val failingRanges = failingRanges(sortedReports, rangeFactory)
                "- parameter ${it.name}: " +
                        failingRanges.joinToString { range ->
                            if (range.start == range.endInclusive) range.start.toString()
                            else "[${range.start}…${range.endInclusive}]"
                        }
            }
    }

    private inline fun <reified T> failingValuesReports(
        targets: List<ForgeTarget<*>>,
        invocationReports: List<InvocationReport>,
        comparator: Comparator<T>
    ): List<String> {
        return targets.filter { it.value is T }
            .map { target ->
                val failingValues = invocationReports.reportForParam<T>(target.name)
                    .filter { it.exception != null }
                    .map { it.target.value }
                    .sortedWith(comparator)
                "- parameter ${target.name}: " + failingValues.joinToString()
            }
    }

    private fun <T : Comparable<T>> failingRanges(
        reports: List<InjectionReport<T>>,
        rangeFactory: (T, T) -> ClosedRange<T>
    ): List<ClosedRange<T>> {
        val failingRanges = mutableListOf<ClosedRange<T>>()
        val last = reports.foldIndexed(null as ClosedRange<T>?) { i, acc, next ->
            if (next.exception == null) {
                if (acc != null) failingRanges.add(acc)
                null
            } else {
                if (acc == null) {
                    rangeFactory(next.target.value, next.target.value)
                } else {
                    rangeFactory(acc.start, next.target.value)
                }
            }
        }
        if (last != null) failingRanges.add(last)
        return failingRanges
    }

    // endregion

    // region Internal

    private fun resetSeed(context: ExtensionContext) {
        val configurations = getConfigurations(context)
        val seed = configurations.map { it.seed }
            .firstOrNull { it != 0L }
        instanceForge.seed = seed ?: Forge.seed()
    }

    private fun getConfigurations(context: ExtensionContext): List<ForgeConfiguration> {
        val result = mutableListOf<ForgeConfiguration>()
        var currentContext = context

        while (currentContext != context.root) {
            val annotation = findAnnotation<ForgeConfiguration>(
                currentContext.element,
                ForgeConfiguration::class.java
            )

            if (annotation.isPresent) {
                result.add(annotation.get())
            }

            if (currentContext.parent.isPresent) {
                currentContext = currentContext.parent.get()
            } else {
                break
            }
        }

        return result
    }

    private fun getConfigurators(context: ExtensionContext): List<ForgeConfigurator> {
        return getConfigurations(context)
            .map { it.value.java.newInstance() }
    }

    // endregion

    companion object {

        /**
         * The key used to store the [Forge] in an [ExtensionContext]'s global store.
         */
        @JvmField
        val EXTENSION_STORE_FORGE_KEY = "${ForgeExtension::class.java.canonicalName}.forge"

        /**
         * Retrieves the [Forge] stored in the given [ExtensionContext] global store.
         * @param context the current [ExtensionContext].
         * @return the valid forge for that context or null
         */
        @JvmStatic
        fun getForge(context: ExtensionContext): Forge? {
            val globalStore = context.getStore(ExtensionContext.Namespace.GLOBAL)
            val storedForge = globalStore.get(EXTENSION_STORE_FORGE_KEY)
            return storedForge as? Forge
        }

        private val STORE_NAMESPACE = ExtensionContext.Namespace.create("fr.xgouchet.elmyr.junit5")
        private val STORE_SHRINK_KEY = "${ForgeExtension::class.java.canonicalName}.shrink"
    }
}
