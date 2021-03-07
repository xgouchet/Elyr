package fr.xgouchet.elmyr.inject

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.AdvancedForgery
import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.DoubleForgery
import fr.xgouchet.elmyr.annotation.FloatForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.IntForgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.MapForgery
import fr.xgouchet.elmyr.annotation.PairForgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType
import fr.xgouchet.elmyr.inject.reflect.invokePrivate
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.javaType

/**
 * The default implementation of a [ForgeryInjector].
 *
 * It can inject forgeries both in Java fields and Kotlin properties.
 */
@Suppress("LiftReturnOrAssignment")
class DefaultForgeryInjector : ForgeryInjector {

    // region ForgeryInjector

    override fun inject(
        forge: Forge,
        target: Any,
        listener: ForgeryInjector.Listener?
    ) {
        val classesToProcess = mutableSetOf<KClass<*>>()
        classesToProcess.add(target.javaClass.kotlin)

        while (classesToProcess.isNotEmpty()) {
            val classToProcess = classesToProcess.first()
            if (classToProcess != Any::class) {
                injectInClass(forge, classToProcess, target, listener)
                classToProcess.superclasses.forEach {
                    classesToProcess.add(it)
                }
            }
            classesToProcess.remove(classToProcess)
        }
    }

    // endregion

    // region Internal

    @Suppress("ThrowableNotThrown")
    private fun injectInClass(
        forge: Forge,
        clazz: KClass<*>,
        target: Any,
        listener: ForgeryInjector.Listener?
    ) {
        val invalidProperties = clazz.declaredMembers
            .filterIsInstance<KProperty<*>>()
            .filter { it !is KMutableProperty }
            .filter {
                it.annotations.any { annotation ->
                    annotation.annotationClass in knownAnnotations
                }
            }
        when (invalidProperties.size) {
            0 -> injectInClassSafe(clazz, forge, target, listener)
            1 -> throw ForgeryInjectorException.withProperty(target, invalidProperties.first())
            else -> throw ForgeryInjectorException.withProperties(target, invalidProperties)
        }
    }

    @Suppress("TooGenericExceptionCaught")
    private fun injectInClassSafe(
        clazz: KClass<*>,
        forge: Forge,
        target: Any,
        listener: ForgeryInjector.Listener?
    ) {
        val mutableProperties = clazz.declaredMembers.filterIsInstance<KMutableProperty<*>>()

        val errors = mutableListOf<Throwable>()

        for (property in mutableProperties) {
            try {
                injectInProperty(forge, clazz, property, target, listener)
            } catch (e: Throwable) {
                errors.add(e)
            }
        }

        if (errors.isNotEmpty()) {
            throw ForgeryInjectorException.withErrors(target, errors)
        }
    }

    private fun injectInProperty(
        forge: Forge,
        clazz: KClass<*>,
        property: KMutableProperty<*>,
        target: Any,
        listener: ForgeryInjector.Listener?
    ) {
        var value: Any? = null
        for (annotation in property.annotations) {
            value = when (annotation) {
                is Forgery -> getForgery(forge, property)
                is BoolForgery -> getBooleanForgery(forge, annotation, property)
                is IntForgery -> getIntForgery(forge, annotation, property)
                is LongForgery -> getLongForgery(forge, annotation, property)
                is FloatForgery -> getFloatForgery(forge, annotation, property)
                is DoubleForgery -> getDoubleForgery(forge, annotation, property)
                is StringForgery -> getStringForgery(forge, annotation, property)
                is AdvancedForgery -> getAdvancedForgery(forge, annotation, property)
                is MapForgery -> getMapForgery(forge, annotation, property)
                is PairForgery -> getPairForgery(forge, annotation, property)
                else -> null
            }
            if (value != null) break
        }

        if (value != null) {
            property.setter.invokePrivate(target, value)
            listener?.onFieldInjected(
                clazz.java,
                property.returnType.javaType,
                property.name,
                value
            )
        }
    }

    private fun getForgery(
        forge: Forge,
        property: KMutableProperty<*>
    ): Any {
        return getGenericForgery(property.returnType, forge)
    }

    private fun getBooleanForgery(
        forge: Forge,
        annotation: BoolForgery,
        property: KMutableProperty<*>
    ): Any {
        check(annotation.probability >= 0f) {
            "You can only use an BoolForgery with a probability between 0f and 1f"
        }
        check(annotation.probability <= 1f) {
            "You can only use an BoolForgery with a probability between 0f and 1f"
        }
        val forgery: Forge.() -> Boolean = { aBool(annotation.probability) }
        return getPrimitiveForgery(property.returnType, Boolean::class, forge, forgery)
    }

    private fun getIntForgery(
        forge: Forge,
        annotation: IntForgery,
        property: KMutableProperty<*>
    ): Any {
        val forgery: Forge.() -> Int
        if (annotation.standardDeviation >= 0) {
            check(annotation.min == Int.MIN_VALUE) {
                "You can only use an IntForgery with min and max or with mean and standardDeviation"
            }
            check(annotation.max == Int.MAX_VALUE) {
                "You can only use an IntForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aGaussianInt(annotation.mean, annotation.standardDeviation) }
        } else {
            check(annotation.mean == 0) {
                "You can only use an IntForgery with min and max or with mean and standardDeviation"
            }
            forgery = { anInt(annotation.min, annotation.max) }
        }
        return getPrimitiveForgery(property.returnType, Int::class, forge, forgery)
    }

    private fun getLongForgery(
        forge: Forge,
        annotation: LongForgery,
        property: KMutableProperty<*>
    ): Any {
        val forgery: Forge.() -> Long
        if (annotation.standardDeviation >= 0) {
            check(annotation.min == Long.MIN_VALUE) {
                "You can only use an LongForgery with min and max or with mean and standardDeviation"
            }
            check(annotation.max == Long.MAX_VALUE) {
                "You can only use an LongForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aGaussianLong(annotation.mean, annotation.standardDeviation) }
        } else {
            check(annotation.mean == 0L) {
                "You can only use an LongForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aLong(annotation.min, annotation.max) }
        }
        return getPrimitiveForgery(property.returnType, Long::class, forge, forgery)
    }

    private fun getFloatForgery(
        forge: Forge,
        annotation: FloatForgery,
        property: KMutableProperty<*>
    ): Any {
        val forgery: Forge.() -> Float
        if (!annotation.standardDeviation.isNaN()) {
            check(annotation.min == -Float.MAX_VALUE) {
                "You can only use an FloatForgery with min and max or with mean and standardDeviation"
            }
            check(annotation.max == Float.MAX_VALUE) {
                "You can only use an FloatForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aGaussianFloat(annotation.mean, annotation.standardDeviation) }
        } else {
            check(annotation.mean == 0f) {
                "You can only use an FloatForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aFloat(annotation.min, annotation.max) }
        }

        return getPrimitiveForgery(property.returnType, Float::class, forge, forgery)
    }

    private fun getDoubleForgery(
        forge: Forge,
        annotation: DoubleForgery,
        property: KMutableProperty<*>
    ): Any {
        val forgery: Forge.() -> Double
        if (!annotation.standardDeviation.isNaN()) {
            check(annotation.min == -Double.MAX_VALUE) {
                "You can only use an DoubleForgery with min and max or with mean and standardDeviation"
            }
            check(annotation.max == Double.MAX_VALUE) {
                "You can only use an DoubleForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aGaussianDouble(annotation.mean, annotation.standardDeviation) }
        } else {
            check(annotation.mean == 0.0) {
                "You can only use an DoubleForgery with min and max or with mean and standardDeviation"
            }
            forgery = { aDouble(annotation.min, annotation.max) }
        }

        return getPrimitiveForgery(property.returnType, Double::class, forge, forgery)
    }

    private fun getStringForgery(
        forge: Forge,
        annotation: StringForgery,
        property: KMutableProperty<*>
    ): Any {
        val forgery: Forge.() -> String
        if (annotation.regex.isNotEmpty()) {
            forgery = { aStringMatching(annotation.regex) }
        } else {
            forgery = when (annotation.type) {
                StringForgeryType.ALPHABETICAL -> { ->
                    anAlphabeticalString(
                        annotation.case,
                        annotation.size
                    )
                }
                StringForgeryType.ALPHA_NUMERICAL -> { ->
                    anAlphaNumericalString(
                        annotation.case,
                        annotation.size
                    )
                }
                StringForgeryType.NUMERICAL -> { -> aNumericalString(annotation.size) }
                StringForgeryType.HEXADECIMAL -> { ->
                    anHexadecimalString(
                        annotation.case,
                        annotation.size
                    )
                }
                StringForgeryType.WHITESPACE -> { -> aWhitespaceString(annotation.size) }
                StringForgeryType.ASCII -> { -> anAsciiString(annotation.size) }
                StringForgeryType.ASCII_EXTENDED -> { -> anExtendedAsciiString(annotation.size) }
            }
        }
        return getPrimitiveForgery(property.returnType, String::class, forge, forgery)
    }

    private fun getAdvancedForgery(
        forge: Forge,
        annotation: AdvancedForgery,
        property: KMutableProperty<*>
    ): Any? {
        return when {
            annotation.string.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.string)
                getStringForgery(forge, usingAnnotation, property)
            }
            annotation.int.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.int)
                getIntForgery(forge, usingAnnotation, property)
            }
            annotation.long.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.long)
                getLongForgery(forge, usingAnnotation, property)
            }
            annotation.float.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.float)
                getFloatForgery(forge, usingAnnotation, property)
            }
            annotation.double.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.double)
                getDoubleForgery(forge, usingAnnotation, property)
            }
            annotation.map.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.map)
                getMapForgery(forge, usingAnnotation, property)
            }
            annotation.pair.isNotEmpty() -> {
                val usingAnnotation = forge.anElementFrom(*annotation.pair)
                getPairForgery(forge, usingAnnotation, property)
            }
            else -> {
                getForgery(forge, property)
            }
        }
    }

    private fun getMapForgery(
        forge: Forge,
        annotation: MapForgery,
        property: KMutableProperty<*>
    ): Map<Any?, Any?> {
        val mapType = property.returnType
        check(mapType.classifier in knownMaps)

        val keyAnnotation = annotation.key
        val keyType = mapType.arguments[0].type ?: uninjectable(property)
        val keyProperty = KInjectedProperty<Any>(property.name, keyType)
        val valueAnnotation = annotation.value
        val valueType = mapType.arguments[1].type ?: uninjectable(property)
        val valueProperty = KInjectedProperty<Any>(property.name, valueType)

        return forge.aMap {
            val forgedKey = getAdvancedForgery(forge, keyAnnotation, keyProperty)
            val forgedValue = getAdvancedForgery(forge, valueAnnotation, valueProperty)
            forgedKey to forgedValue
        }
    }

    private fun getPairForgery(
        forge: Forge,
        annotation: PairForgery,
        property: KMutableProperty<*>
    ): Pair<Any?, Any?> {
        val pairType = property.returnType
        check(pairType.classifier in knownPairs)

        val firstAnnotation = annotation.first
        val firstType = pairType.arguments[0].type ?: uninjectable(property)
        val firstProperty = KInjectedProperty<Any>(property.name, firstType)
        val secondAnnotation = annotation.second
        val secondType = pairType.arguments[1].type ?: uninjectable(property)
        val secondProperty = KInjectedProperty<Any>(property.name, secondType)

        val forgedKey = getAdvancedForgery(forge, firstAnnotation, firstProperty)
        val forgedValue = getAdvancedForgery(forge, secondAnnotation, secondProperty)
        return forgedKey to forgedValue
    }

    private fun getGenericForgery(type: KType, forge: Forge): Any {
        val arguments = type.arguments
        val classifier = type.classifier
        if (classifier !is KClass<*>) uninjectable(classifier)

        return if (arguments.isEmpty()) {
            forge.getForgery(classifier.java)
        } else {
            getParameterizedForgery(forge, arguments, classifier)
        }
    }

    @Suppress("UnsafeCallOnNullableType")
    private fun getParameterizedForgery(
        forge: Forge,
        arguments: List<KTypeProjection>,
        classifier: KClass<*>
    ): Any {
        return when (classifier) {
            in knownLists -> forge.aList { getGenericForgery(arguments[0].type!!, forge) }
            in knownSets -> forge.aList { getGenericForgery(arguments[0].type!!, forge) }.toSet()
            in knownMaps -> forge.aMap {
                val key = getGenericForgery(arguments[0].type!!, forge)
                val value = getGenericForgery(arguments[1].type!!, forge)
                key to value
            }
            else -> uninjectable(classifier)
        }
    }

    private fun getPrimitiveForgery(
        type: KType,
        klass: KClass<*>,
        forge: Forge,
        forgery: Forge.() -> Any
    ): Any {
        val arguments = type.arguments
        val classifier = type.classifier
        check(classifier is KClass<*>)

        return if (arguments.isEmpty()) {
            check(classifier == klass || classifier.java == klass.java)
            forge.forgery()
        } else {
            getParameterizedPrimitiveForgery(arguments, classifier, klass, forge, forgery)
        }
    }

    @Suppress("UnsafeCallOnNullableType")
    private fun getParameterizedPrimitiveForgery(
        arguments: List<KTypeProjection>,
        classifier: KClass<*>,
        klass: KClass<*>,
        forge: Forge,
        forgery: Forge.() -> Any
    ): Any {
        return when (classifier) {
            in knownLists -> forge.aList {
                getPrimitiveForgery(
                    arguments[0].type!!,
                    klass,
                    forge,
                    forgery
                )
            }
            in knownSets -> forge.aList {
                getPrimitiveForgery(
                    arguments[0].type!!,
                    klass,
                    forge,
                    forgery
                )
            }.toSet()
            else -> uninjectable(classifier)
        }
    }

    private fun uninjectable(property: KMutableProperty<*>): Nothing {
        throw ForgeryInjectorException("Unable to forge $property.")
    }

    private fun uninjectable(klass: KClassifier?): Nothing {
        throw ForgeryInjectorException("Unable to forge property with class $klass.")
    }

    @Suppress("NotImplementedDeclaration")
    private class KInjectedProperty<R>(
        override val name: String,
        override val returnType: KType
    ) : KMutableProperty<R> {
        override val annotations: List<Annotation> = emptyList()
        override val isAbstract: Boolean = false
        override val isConst: Boolean = false
        override val isFinal: Boolean = false
        override val isLateinit: Boolean = true
        override val isOpen: Boolean = false
        override val isSuspend: Boolean = false
        override val parameters: List<KParameter> = emptyList()
        override val typeParameters: List<KTypeParameter> = emptyList()
        override val visibility: KVisibility? = KVisibility.PUBLIC
        override val getter: KProperty.Getter<R>
            get() = TODO()
        override val setter: KMutableProperty.Setter<R>
            get() = TODO()

        override fun call(vararg args: Any?): R {
            TODO()
        }

        override fun callBy(args: Map<KParameter, Any?>): R {
            TODO()
        }
    }

    // endregion

    companion object {
        private val knownAnnotations = setOf<KClass<*>>(
            Forgery::class,
            BoolForgery::class,
            IntForgery::class,
            LongForgery::class,
            FloatForgery::class,
            DoubleForgery::class,
            StringForgery::class,
            AdvancedForgery::class,
            MapForgery::class
        )
        private val knownLists = setOf<KClass<*>>(
            List::class, Collection::class
        )
        private val knownSets = setOf<KClass<*>>(
            Set::class
        )
        private val knownMaps = setOf<KClass<*>>(
            Map::class
        )
        private val knownPairs = setOf<KClass<*>>(
            Pair::class
        )
    }
}
