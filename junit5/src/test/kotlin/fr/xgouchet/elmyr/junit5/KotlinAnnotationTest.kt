package fr.xgouchet.elmyr.junit5

import fr.xgouchet.elmyr.Case
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeConfigurator
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
import fr.xgouchet.elmyr.junit5.fixture.Bar
import fr.xgouchet.elmyr.junit5.fixture.BarFactory
import fr.xgouchet.elmyr.junit5.fixture.Foo
import fr.xgouchet.elmyr.junit5.fixture.FooFactory
import java.time.Month
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(ForgeExtension::class)
@ForgeConfiguration(KotlinAnnotationTest.Configurator::class)
internal open class KotlinAnnotationTest {

    @Forgery
    lateinit var fakeFoo: Foo

    @Forgery
    lateinit var fakeFooList: List<Foo>

    @Forgery
    lateinit var fakeFooSet: Set<Foo>

    @Forgery
    lateinit var fakeFooMap: Map<Foo, Bar>

    @Forgery
    lateinit var fakeMonth: Month

    // region Forge

    @Test
    fun testRun1(@Forgery forge: Forge) {
        checkSeedChanged(forge)
        checkForgeryInjectedInField()
    }

    @Test
    fun testRun2(@Forgery forge: Forge) {
        checkSeedChanged(forge)
        checkForgeryInjectedInField()
    }

    @Test
    fun testRun3(forge: Forge) {
        checkSeedChanged(forge)
        checkForgeryInjectedInField()
    }

    // endregion

    // region primitive

    @Test
    fun injectIntWithDefaultProbability(@BoolForgery b: Boolean) {
        checkForgeryInjectedInField()
    }

    @Test
    fun injectIntWithCustomProbability(@BoolForgery(probability = 0.001f) b: Boolean) {
        checkForgeryInjectedInField()
    }

    @Test
    fun injectIntWithDefaultRange(@IntForgery i: Int) {
        assertThat(i).isStrictlyBetween(Int.MIN_VALUE, Int.MAX_VALUE)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectIntWithCustomRange(@IntForgery(min = 13, max = 42) i: Int) {
        assertThat(i).isBetween(13, 41)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectIntWithGaussianDistribution(@IntForgery(mean = 42, standardDeviation = 7) i: Int) {
        assertThat(i).isBetween(-58, 142)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectLongWithDefaultRange(@LongForgery l: Long) {
        assertThat(l).isStrictlyBetween(Long.MIN_VALUE, Long.MAX_VALUE)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectLongWithCustomRange(@LongForgery(min = 13L, max = 42L) l: Long) {
        assertThat(l).isBetween(13L, 41L)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectLongWithGaussianDistribution(@LongForgery(mean = 42L, standardDeviation = 7L) l: Long) {
        assertThat(l).isBetween(-58L, 142L)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectFloatWithDefaultRange(@FloatForgery f: Float) {
        assertThat(f).isStrictlyBetween(-Float.MAX_VALUE, Float.MAX_VALUE)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectFloatWithCustomRange(@FloatForgery(min = 13f, max = 42f) f: Float) {
        assertThat(f).isBetween(13f, 42f)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectFloatWithGaussianDistribution(@FloatForgery(mean = 42f, standardDeviation = 7f) f: Float) {
        assertThat(f).isBetween(-58f, 142f)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectDoubleWithDefaultRange(@DoubleForgery d: Double) {
        assertThat(d).isStrictlyBetween(-Double.MAX_VALUE, Double.MAX_VALUE)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectDoubleWithCustomRange(@DoubleForgery(min = 13.0, max = 42.0) d: Double) {
        assertThat(d).isBetween(13.0, 42.0)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectDoubleWithGaussianDistribution(@DoubleForgery(mean = 42.0, standardDeviation = 7.0) d: Double) {
        assertThat(d).isBetween(-58.0, 142.0)
        checkForgeryInjectedInField()
    }

    // endregion

    // region Strings

    @Test
    fun injectStringWithRegex(@StringForgery(regex = TEST_REGEX) s: String) {
        assertThat(s).matches(TEST_REGEX)
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAsciiString(@StringForgery(StringForgeryType.ASCII) s: String) {
        s.forEach {
            assertThat(it)
                .isGreaterThanOrEqualTo(MIN_PRINTABLE)
                .isLessThanOrEqualTo(MAX_ASCII)
        }
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAsciiExtendedString(@StringForgery(StringForgeryType.ASCII_EXTENDED) s: String) {
        s.forEach {
            assertThat(it)
                .isGreaterThanOrEqualTo(MIN_PRINTABLE)
                .isLessThanOrEqualTo(MAX_ASCII_EXTENDED)
        }
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphabeticalString(@StringForgery(StringForgeryType.ALPHABETICAL) s: String) {
        assertThat(s).matches("[a-zA-Z]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphabeticalLowercaseString(
        @StringForgery(StringForgeryType.ALPHABETICAL, Case.LOWER) s: String
    ) {
        assertThat(s).matches("[a-z]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphabeticalUppercaseString(
        @StringForgery(StringForgeryType.ALPHABETICAL, Case.UPPER) s: String
    ) {
        assertThat(s).matches("[A-Z]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphaNumericalString(@StringForgery(StringForgeryType.ALPHA_NUMERICAL) s: String) {
        assertThat(s).matches("[a-zA-Z0-9]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphaNumericalStringWithSize(@StringForgery(StringForgeryType.ALPHA_NUMERICAL, size = 42) s: String) {
        assertThat(s).matches("[a-zA-Z0-9]{42}")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphaNumericalLowercaseString(
        @StringForgery(StringForgeryType.ALPHA_NUMERICAL, Case.LOWER) s: String
    ) {
        assertThat(s).matches("[a-z0-9]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectAlphaNumericalUpercaseString(
        @StringForgery(StringForgeryType.ALPHA_NUMERICAL, Case.UPPER) s: String
    ) {
        assertThat(s).matches("[A-Z0-9]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectWhitespaceString(@StringForgery(StringForgeryType.WHITESPACE) s: String) {
        assertThat(s).matches("\\s+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectHexadecimalString(@StringForgery(StringForgeryType.HEXADECIMAL) s: String) {
        assertThat(s).matches("[a-fA-F0-9]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectHexadecimalLowercaseString(
        @StringForgery(StringForgeryType.HEXADECIMAL, Case.LOWER) s: String
    ) {
        assertThat(s).matches("[a-f0-9]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectHexadecimalUppercaseString(
        @StringForgery(StringForgeryType.HEXADECIMAL, Case.UPPER) s: String
    ) {
        assertThat(s).matches("[A-F0-9]+")
        checkForgeryInjectedInField()
    }

    @Test
    fun injectNumericalString(@StringForgery(StringForgeryType.NUMERICAL) s: String) {
        assertThat(s).matches("[0-9]+")
        checkForgeryInjectedInField()
    }

    // endregion

    // region Enum

    @Test
    fun injectEnumForgery(@Forgery month: Month) {
        assertThat(month)
            .isNotNull()
    }

    // endregion

    // region Object from Factory

    @Test
    fun injectForgery(@Forgery foo: Foo) {
        assertThat(foo).isNotNull()
        checkForgeryInjectedInField()
    }

    // endregion

    // region Collections

    @Test
    fun injectCollection(@Forgery list: List<Foo>) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach {
            assertThat(it).isInstanceOf(Foo::class.java)
        }
    }

    @Test
    fun injectCollection(@Forgery collection: Collection<Foo>) {
        assertThat(collection).isNotNull.isNotEmpty

        collection.forEach {
            assertThat(it).isInstanceOf(Foo::class.java)
        }
    }

    @Test
    fun injectList(@Forgery list: List<Foo>) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach {
            assertThat(it).isInstanceOf(Foo::class.java)
        }
    }

    @Test
    fun injectSet(@Forgery set: Set<Foo>) {
        assertThat(set).isNotNull.isNotEmpty

        set.forEach {
            assertThat(it).isInstanceOf(Foo::class.java)
        }
    }

    @Test
    fun injectNestedCollection(@Forgery nested: List<Set<Foo>>) {
        assertThat(nested).isNotNull.isNotEmpty

        nested.forEach { set ->
            assertThat(set).isNotNull.isNotEmpty
            set.forEach {
                assertThat(it).isInstanceOf(Foo::class.java)
            }
        }
    }

    @Test
    fun injectGenericMap(@Forgery map: Map<Foo, Bar>) {
        assertThat(map).isNotNull.isNotEmpty

        map.forEach {
            assertThat(it.key).isInstanceOf(Foo::class.java)
            assertThat(it.value).isInstanceOf(Bar::class.java)
        }
    }

    // endregion

    // region Advanced

    @Test
    fun injectAdvancedString(
        @AdvancedForgery(
            string = [StringForgery(StringForgeryType.ALPHABETICAL), StringForgery(StringForgeryType.NUMERICAL)]
        ) s: String
    ) {
        assertThat(s).matches("([a-zA-Z]+)|([0-9]+)")
    }

    @Test
    fun injectAdvancedStringList(
        @AdvancedForgery(
            string = [StringForgery(StringForgeryType.ALPHABETICAL), StringForgery(StringForgeryType.NUMERICAL)]
        ) list: List<String>
    ) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach {
            assertThat(it).matches("([a-zA-Z]+)|([0-9]+)")
        }
    }

    @Test
    fun injectAdvancedInt(
        @AdvancedForgery(
            int = [IntForgery(4, 15), IntForgery(23, 42)]
        ) i: Int
    ) {
        assertThat(i).matches { it in 4 until 15 || it in 23 until 42 }
    }

    @Test
    fun injectAdvancedIntList(
        @AdvancedForgery(
            int = [IntForgery(4, 15), IntForgery(23, 42)]
        ) list: List<Int>
    ) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach { i ->
            assertThat(i).matches { it in 4 until 15 || it in 23 until 42 }
        }
    }

    @Test
    fun injectAdvancedLong(
        @AdvancedForgery(
            long = [LongForgery(4L, 15L), LongForgery(23L, 42L)]
        ) i: Long
    ) {
        assertThat(i).matches { it in 4L until 15L || it in 23L until 42L }
    }

    @Test
    fun injectAdvancedLongList(
        @AdvancedForgery(
            long = [LongForgery(4L, 15L), LongForgery(23L, 42L)]
        ) list: List<Long>
    ) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach { i ->
            assertThat(i).matches { it in 4L until 15L || it in 23L until 42L }
        }
    }

    @Test
    fun injectAdvancedFloat(
        @AdvancedForgery(
            float = [FloatForgery(4f, 15f), FloatForgery(23f, 42f)]
        ) i: Float
    ) {
        assertThat(i).matches { it in 4f.rangeTo(15f) || it in 23f.rangeTo(42f) }
    }

    @Test
    fun injectAdvancedFloatList(
        @AdvancedForgery(
            float = [FloatForgery(4f, 15f), FloatForgery(23f, 42f)]
        ) list: List<Float>
    ) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach { i ->
            assertThat(i).matches { it in 4f.rangeTo(15f) || it in 23f.rangeTo(42f) }
        }
    }

    @Test
    fun injectAdvancedDouble(
        @AdvancedForgery(
            double = [DoubleForgery(4.0, 15.0), DoubleForgery(23.0, 42.0)]
        ) i: Double
    ) {
        assertThat(i).matches { it in 4.0.rangeTo(15.0) || it in 23.0.rangeTo(42.0) }
    }

    @Test
    fun injectAdvancedDoubleList(
        @AdvancedForgery(
            double = [DoubleForgery(4.0, 15.0), DoubleForgery(23.0, 42.0)]
        ) list: List<Double>
    ) {
        assertThat(list).isNotNull.isNotEmpty

        list.forEach { i ->
            assertThat(i).matches { it in 4.0.rangeTo(15.0) || it in 23.0.rangeTo(42.0) }
        }
    }

    // endregion

    // region Map

    @Test
    fun injectMap(
        @MapForgery(
            key = AdvancedForgery(string = [StringForgery(StringForgeryType.HEXADECIMAL)]),
            value = AdvancedForgery(long = [LongForgery(10L)])
        ) map: Map<String, Long>
    ) {
        assertThat(map.entries)
            .isNotEmpty()
            .allMatch {
                it.key.matches(Regex("[a-fA-F0-9]+")) && it.value > 0L
            }
    }

    @Test
    fun injectMapWithGenericValue(
        @MapForgery(
            key = AdvancedForgery(string = [StringForgery(regex = "\\w+@\\w+\\.[a-z]{3}")])
        ) map: Map<String, Foo>
    ) {
        assertThat(map.entries)
            .isNotEmpty()
            .allMatch {
                it.key.matches(Regex("\\w+@\\w+\\.[a-z]{3}")) && it.value != null
            }
    }

    @Test
    fun injectNestedMap(
        @MapForgery(
            key = AdvancedForgery(string = [StringForgery(StringForgeryType.NUMERICAL)]),
            value = AdvancedForgery(
                map = [
                    MapForgery(
                        key = AdvancedForgery(int = [IntForgery(10, 100)])
                    )
                ]
            )
        ) map: Map<String, Map<Int, Foo>>
    ) {
        assertThat(map.entries)
            .isNotEmpty()
            .allMatch {
                it.key.matches(Regex("[0-9]+")) && it.value.isNotEmpty() && it.value.entries.all { nested ->
                    nested.key >= 10 && nested.key <= 100 && nested.value != null
                }
            }
    }

    // endregion

    // region Pair

    @Test
    fun injectPair(
        @PairForgery(
            first = AdvancedForgery(string = [StringForgery(StringForgeryType.HEXADECIMAL)]),
            second = AdvancedForgery(long = [LongForgery(10L)])
        ) pair: Pair<String, Long>
    ) {
        assertThat(pair.first).matches("[a-fA-F0-9]+")
        assertThat(pair.second).isGreaterThan(10L)
    }

    @Test
    fun injectPairWithGenericSecond(
        @PairForgery(
            first = AdvancedForgery(string = [StringForgery(regex = "\\w+@\\w+\\.[a-z]{3}")])
        ) pair: Pair<String, Foo>
    ) {
        assertThat(pair.first).matches("\\w+@\\w+\\.[a-z]{3}")
        assertThat(pair.second).isNotNull()
    }

    @Test
    fun injectNestedPair(
        @PairForgery(
            first = AdvancedForgery(string = [StringForgery(StringForgeryType.NUMERICAL)]),
            second = AdvancedForgery(
                pair = [
                    PairForgery(
                        first = AdvancedForgery(int = [IntForgery(10, 100)])
                    )
                ]
            )
        ) pair: Pair<String, Pair<Int, Foo>>
    ) {

        assertThat(pair.first).matches("\\d+")
        assertThat(pair.second.first).isBetween(10, 100)
        assertThat(pair.second.second).isNotNull()
    }

    // endregion

    // region Internal

    private fun checkSeedChanged(forge: Forge) {
        val previousSeed = memoizedSeed
        if (previousSeed != null) {
            Assertions.assertThat(forge.seed).isNotEqualTo(previousSeed)
        }
        memoizedSeed = forge.seed
    }

    private fun checkForgeryInjectedInField() {
        val previousFoo = memoizedFoo
        check(::fakeFoo.isInitialized)
        if (previousFoo != null) {
            assertThat(fakeFoo).isNotEqualTo(previousFoo)
        }
        memoizedFoo = fakeFoo

        assertThat(fakeFoo).isNotNull()
        assertThat(fakeFooList).isNotNull.isNotEmpty
        assertThat(fakeFooSet).isNotNull.isNotEmpty
        assertThat(fakeFooMap).isNotNull.isNotEmpty

        assertThat(fakeMonth).isNotNull()
    }

    // endregion

    class Configurator : ForgeConfigurator {
        override fun configure(forge: Forge) {
            forge.addFactory(Foo::class.java, FooFactory())
            forge.addFactory(Bar::class.java, BarFactory())
        }
    }

    companion object {
        internal var memoizedSeed: Long? = null
        internal var memoizedFoo: Foo? = null

        internal const val MIN_PRINTABLE = 0x20.toChar()
        internal const val MAX_ASCII = 0x7F.toChar()
        internal const val MAX_ASCII_EXTENDED = 0xFF.toChar()

        internal const val TEST_REGEX = "[a-z](\\d+)[_\\.\\-][A-Z]*"
    }
}
