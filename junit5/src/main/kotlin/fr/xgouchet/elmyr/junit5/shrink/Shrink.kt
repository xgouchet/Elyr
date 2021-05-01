package fr.xgouchet.elmyr.junit5.shrink

import org.junit.jupiter.api.TestTemplate
import org.junit.platform.commons.annotation.Testable

/**
 * Annotate a test method to enable shrinking.
 *
 * Make sure to also add the [TestTemplate] annotation.
 *
 * @param maximumRunCount the maximum number of run count to perform.
 */
@Testable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Shrink(
    val maximumRunCount: Int = 16
)
