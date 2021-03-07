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
import fr.xgouchet.elmyr.junit5.params.IntForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.LongForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.MapForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.PairForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.RegexForgeryParamResolver
import fr.xgouchet.elmyr.junit5.params.StringForgeryParamResolver
import java.lang.reflect.Constructor
import java.lang.reflect.Type
import java.util.Locale
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler
import org.junit.platform.commons.support.AnnotationSupport

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
    TestExecutionExceptionHandler,
    ParameterResolver,
    ForgeryInjector.Listener {

    internal val instanceForge: Forge = Forge()
    private val injector: ForgeryInjector = DefaultForgeryInjector()

    private val injectedData: MutableList<ForgeTarget<*>> = mutableListOf()

    private val parameterResolvers = listOf(
        ForgeParamResolver,
        BooleanForgeryParamResolver,
        IntForgeryParamResolver,
        LongForgeryParamResolver,
        FloatForgeryParamResolver,
        DoubleForgeryParamResolver,
        StringForgeryParamResolver,
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

    // region TestExecutionExceptionHandler

    /** @inheritdoc */
    override fun handleTestExecutionException(context: ExtensionContext, throwable: Throwable) {
        val configuration = getConfigurations(context).firstOrNull()
        val errorMessage = "<%s.%s()> failed with Forge seed 0x%xL".format(
            Locale.US,
            context.requiredTestInstance.javaClass.simpleName,
            context.requiredTestMethod.name,
            instanceForge.seed
        )

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
        System.err.println(
            errorMessage + injectedMessage + helpMessage
        )

        throw throwable
    }

    // endregion

    // region ParameterResolver

    /** @inheritdoc */
    override fun supportsParameter(
        parameterContext: ParameterContext,
        extensionContext: ExtensionContext
    ): Boolean {
        val isSupported = parameterResolvers.any {
            it.supportsParameter(parameterContext, extensionContext)
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
        val resolver = parameterResolvers.firstOrNull {
            it.supportsParameter(parameterContext, extensionContext)
        }
        val value = resolver?.resolveParameter(parameterContext, extensionContext, instanceForge)

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
            val annotation = AnnotationSupport
                .findAnnotation<ForgeConfiguration>(
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
    }
}
