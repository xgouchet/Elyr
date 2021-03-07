package fr.xgouchet.elmyr.junit5

import fr.xgouchet.elmyr.annotation.DoubleForgery
import fr.xgouchet.elmyr.annotation.FloatForgery
import fr.xgouchet.elmyr.annotation.IntForgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.junit5.shrink.Shrink
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ForgeExtension::class)
internal class KotlinShrinkingTest {

    @Disabled
    @Shrink(maximumRunCount = RUN_COUNT_NO_PARAMS)
    @TestTemplate
    fun testNoParams() {
        runCountNoParams++
    }

    @Disabled
    @Shrink(maximumRunCount = RUN_COUNT_WITH_PARAMS)
    @TestTemplate
    fun testWithParams(@IntForgery i: Int) {
        runCountWithParams.add(i)
    }

    @Disabled
    @Shrink(maximumRunCount = RUN_COUNT_FAILING)
    @TestTemplate
    fun testFailing(@IntForgery(0, 65536) i: Int) {
        runCountFailing++

        if ((i in 42..666) || (i in 34605..44523)) assertThat(i).isLessThan(0)
    }

    @Disabled
    @Shrink(maximumRunCount = RUN_COUNT_FAILING)
    @TestTemplate
    fun testFailing(@LongForgery(0L, 65536L) l: Long) {
        runCountFailing++

        if ((l in 42L..666L) || (l in 34605L..44523L)) assertThat(l).isLessThan(0L)
    }

    @Disabled
    @Shrink(maximumRunCount = RUN_COUNT_FAILING)
    @TestTemplate
    fun testFailing(@FloatForgery(0f, 65536f) f: Float) {
        runCountFailing++

        if ((f in 42f..666f) || (f in 34605f..44523f)) assertThat(f).isLessThan(0f)
    }

    @Disabled
    @Shrink(maximumRunCount = RUN_COUNT_FAILING)
    @TestTemplate
    fun testFailing(@DoubleForgery(0.0, 65536.0) d: Double) {
        runCountFailing++

        if ((d in 42.0..666.0) || (d in 34605.0..44523.0)) assertThat(d).isLessThan(0.0)
    }

    companion object {

        @AfterAll
        @JvmStatic
        fun `it's just another brick in the wall`() {
            assertThat(runCountNoParams).isEqualTo(RUN_COUNT_NO_PARAMS)
            assertThat(runCountFailing).isEqualTo(RUN_COUNT_FAILING)

            assertThat(runCountWithParams.size).isEqualTo(RUN_COUNT_WITH_PARAMS)
            assertThat(runCountWithParams.toSet().size).isGreaterThan(RUN_COUNT_WITH_PARAMS / 3)
        }

        const val RUN_COUNT_NO_PARAMS = 11
        const val RUN_COUNT_WITH_PARAMS = 13
        const val RUN_COUNT_FAILING = 2048

        var runCountNoParams = 0
        var runCountFailing = 0
        var runCountWithParams = mutableListOf<Int>()
        var runCountFlaky = mutableListOf<Int>()
    }
}
