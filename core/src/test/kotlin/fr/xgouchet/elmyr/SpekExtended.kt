package fr.xgouchet.elmyr

import org.assertj.core.api.Java6Assertions.assertThat
import org.assertj.core.api.Java6Assertions.within
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * @param T the expected type of throwable to thrown
 * @param block the block to test
 * Defines a throws assertion to use in Spek, shamelessly Ctrl+C/Ctrl+V from
 * https://github.com/encodeering/conflate/commit/efc35c4392076b212cca569b0243ebcf8e7b127f
 */
inline fun <reified T : Throwable> throws(block: () -> Unit) {

    var ex: Throwable? = null
    var thrown = false
    var matches = false

    try {
        block()
    } catch (e: Throwable) {
        ex = e
        matches = T::class.isInstance(e)
        thrown = true
    } finally {
        if (!thrown) throw AssertionError("block should have thrown a ${T::class.simpleName}")
        if (!matches && ex != null) {
            throw AssertionError(
                    "block should have thrown a ${T::class.simpleName}, " +
                            "but threw a ${ex.javaClass.simpleName}"
            )
        }
    }
}

fun verifyGaussianDistribution(
    count: Int,
    expectedMean: Double,
    expectedStandardDev: Double,
    provider: (Int) -> Double
) {
    var sum = 0.0
    var squareSum = 0.0

    for (i in 0 until count) {
        val x = provider(i)
        sum += x
        squareSum += x * x
    }

    val computedMean = sum / count
    val d = squareSum - (count * expectedMean * expectedMean)
    val computedStDev = sqrt(abs(d) / (count - 1.0))
    assertThat(computedMean)
            .isCloseTo(expectedMean, within(expectedStandardDev))
    assertThat(computedStDev)
            .isCloseTo(expectedStandardDev, within(expectedStandardDev * 10))
}

/**
 * @param expectedProbability the expected probability that the operation returns true
 * @param operation an operation returning a boolean
 */
fun verifyProbability(
    expectedProbability: Double,
    operation: () -> Boolean
) {

    val count = 512
    var countTrue = 0.0

    repeat(count) { if (operation()) countTrue++ }

    assertThat(countTrue / count)
            .isCloseTo(expectedProbability, within(0.1))
}