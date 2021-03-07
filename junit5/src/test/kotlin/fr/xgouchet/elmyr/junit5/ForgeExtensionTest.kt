package fr.xgouchet.elmyr.junit5

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import fr.xgouchet.elmyr.Case
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.ForgeryFactoryMissingException
import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.DoubleForgery
import fr.xgouchet.elmyr.annotation.FloatForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.IntForgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType
import fr.xgouchet.elmyr.junit5.fixture.BarFactory
import fr.xgouchet.elmyr.junit5.fixture.Foo
import fr.xgouchet.elmyr.junit5.fixture.FooFactory
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintStream
import java.lang.reflect.Method
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
internal class ForgeExtensionTest {

    lateinit var testedExtension: ForgeExtension

    @Mock
    lateinit var mockExtensionContext: ExtensionContext

    @Mock
    lateinit var mockParameterContext: ParameterContext

    @Mock
    lateinit var mockTarget: Any

    lateinit var fakeMethod: Method

    private lateinit var originalStream: PrintStream
    private lateinit var errStreamContent: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        testedExtension = ForgeExtension()

        fakeMethod = String::class.java.methods.first()

        originalStream = System.err
        errStreamContent = ByteArrayOutputStream()
        System.setErr(PrintStream(errStreamContent))
    }

    @AfterEach
    fun tearDown() {
        System.setErr(originalStream)
    }

    // region ParameterResolver.supportsParameter

    @Test
    fun `supportsParameter with Forgery`() {
        prepareParamContext("withStringForgery")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with BoolForgery`() {
        prepareParamContext("withBool")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with BoolForgery {List}`() {
        prepareParamContext("withBoolList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with BoolForgery {Set}`() {
        prepareParamContext("withBoolSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with BoolForgery {Collection}`() {
        prepareParamContext("withBoolCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with BoolForgery {NestedList}`() {
        prepareParamContext("withBoolNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with IntForgery`() {
        prepareParamContext("withInt")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with IntForgery {List}`() {
        prepareParamContext("withIntList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with IntForgery {Set}`() {
        prepareParamContext("withIntSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with IntForgery {Collection}`() {
        prepareParamContext("withIntCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with IntForgery {NestedList}`() {
        prepareParamContext("withIntNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with LongForgery`() {
        prepareParamContext("withLong")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with LongForgery {List}`() {
        prepareParamContext("withLongList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with LongForgery {Set}`() {
        prepareParamContext("withLongSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with LongForgery {Collection}`() {
        prepareParamContext("withLongCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with LongForgery {NestedList}`() {
        prepareParamContext("withLongNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with FloatForgery`() {
        prepareParamContext("withFloat")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with FloatForgery {List}`() {
        prepareParamContext("withFloatList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with FloatForgery {Set}`() {
        prepareParamContext("withFloatSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with FloatForgery {Collection}`() {
        prepareParamContext("withFloatCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with FloatForgery {NestedList}`() {
        prepareParamContext("withFloatNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with DoubleForgery`() {
        prepareParamContext("withDouble")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with DoubleForgery {List}`() {
        prepareParamContext("withDoubleList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with DoubleForgery {Set}`() {
        prepareParamContext("withDoubleSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with DoubleForgery {Collection}`() {
        prepareParamContext("withDoubleCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with DoubleForgery {NestedList}`() {
        prepareParamContext("withDoubleNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with StringForgery`() {
        prepareParamContext("withString")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with StringForgery {List}`() {
        prepareParamContext("withStringList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with StringForgery {Set}`() {
        prepareParamContext("withStringSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with StringForgery {Collection}`() {
        prepareParamContext("withStringCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with StringForgery {NestedList}`() {
        prepareParamContext("withStringNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with RegexForgery`() {
        prepareParamContext("withRegex")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with RegexForgery {List}`() {
        prepareParamContext("withRegexList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with RegexForgery {Set}`() {
        prepareParamContext("withRegexSet")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with RegexForgery {Collection}`() {
        prepareParamContext("withRegexCollection")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter with RegexForgery {NestedList}`() {
        prepareParamContext("withRegexNestedList")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter returns true on un-annotated and Forge type`() {
        prepareParamContext("withForge")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isTrue()
    }

    @Test
    fun `supportsParameter Fails on Constructor with Forgery`() {
        val fakeConstructor = Reflekta::class.java.constructors.first()
        whenever(mockParameterContext.isAnnotated(Forgery::class.java)) doReturn true
        whenever(mockParameterContext.declaringExecutable) doReturn fakeConstructor

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter returns false on un-annotated`() {
        prepareParamContext("withUnannotatedString")

        val result = testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isFalse()
    }

    @Test
    fun `supportsParameter Fails on BoolForgery param not Bool`() {
        prepareParamContext("withNotBool")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter Fails on IntForgery param not Int`() {
        prepareParamContext("withNotInt")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter Fails on LongForgery param not Long`() {
        prepareParamContext("withNotLong")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter Fails on FloatForgery param not Float`() {
        prepareParamContext("withNotFloat")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter Fails on DoubleForgery param not Double`() {
        prepareParamContext("withNotDouble")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter Fails on StringForgery param not String`() {
        prepareParamContext("withNotString")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `supportsParameter Fails on RegexForgery param not Regex`() {
        prepareParamContext("withNotRegex")

        assertThrows<IllegalStateException> {
            testedExtension.supportsParameter(mockParameterContext, mockExtensionContext)
        }
    }

    // endregion

    // region ParameterResolver.resolveParameter

    @Test
    fun `resolveParameter {Forge}`() {
        prepareParamContext("withForge")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).isSameAs(testedExtension.instanceForge)
    }

    @Test
    fun `resolveParameter {Bool}`() {
        prepareParamContext("withBool")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is Boolean }
    }

    @Test
    fun `resolveParameter {Bool list}`() {
        prepareParamContext("withBoolList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty().allMatch { it is Boolean }
    }

    @Test
    fun `resolveParameter {Bool set}`() {
        prepareParamContext("withBoolSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty().allMatch { it is Boolean }
    }

    @Test
    fun `resolveParameter {Bool collection}`() {
        prepareParamContext("withBoolCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty().allMatch { it is Boolean }
    }

    @Test
    fun `resolveParameter {Bool nested list}`() {
        prepareParamContext("withBoolNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch { it is Set<*> && it.all { b -> b is Boolean } }
    }

    @Test
    fun `resolveParameter {Int}`() {
        prepareParamContext("withInt")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is Int }
    }

    @Test
    fun `resolveParameter {Int list}`() {
        prepareParamContext("withIntList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty().allMatch { it is Int }
    }

    @Test
    fun `resolveParameter {Int set}`() {
        prepareParamContext("withIntSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty().allMatch { it is Int }
    }

    @Test
    fun `resolveParameter {Int collection}`() {
        prepareParamContext("withIntCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty().allMatch { it is Int }
    }

    @Test
    fun `resolveParameter {Int nested list}`() {
        prepareParamContext("withIntNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch { it is Set<*> && it.all { b -> b is Int } }
    }

    @Test
    fun `resolveParameter {Long}`() {
        prepareParamContext("withLong")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is Long }
    }

    @Test
    fun `resolveParameter {Long list}`() {
        prepareParamContext("withLongList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty().allMatch { it is Long }
    }

    @Test
    fun `resolveParameter {Long set}`() {
        prepareParamContext("withLongSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty().allMatch { it is Long }
    }

    @Test
    fun `resolveParameter {Long collection}`() {
        prepareParamContext("withLongCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty().allMatch { it is Long }
    }

    @Test
    fun `resolveParameter {Long nested list}`() {
        prepareParamContext("withLongNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch { it is Set<*> && it.all { b -> b is Long } }
    }

    @Test
    fun `resolveParameter {Float}`() {
        prepareParamContext("withFloat")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is Float }
    }

    @Test
    fun `resolveParameter {Float list}`() {
        prepareParamContext("withFloatList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty().allMatch { it is Float }
    }

    @Test
    fun `resolveParameter {Float set}`() {
        prepareParamContext("withFloatSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty().allMatch { it is Float }
    }

    @Test
    fun `resolveParameter {Float collection}`() {
        prepareParamContext("withFloatCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty().allMatch { it is Float }
    }

    @Test
    fun `resolveParameter {Float nested list}`() {
        prepareParamContext("withFloatNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch { it is Set<*> && it.all { b -> b is Float } }
    }

    @Test
    fun `resolveParameter {Double}`() {
        prepareParamContext("withDouble")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is Double }
    }

    @Test
    fun `resolveParameter {Double list}`() {
        prepareParamContext("withDoubleList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty().allMatch { it is Double }
    }

    @Test
    fun `resolveParameter {Double set}`() {
        prepareParamContext("withDoubleSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty().allMatch { it is Double }
    }

    @Test
    fun `resolveParameter {Double collection}`() {
        prepareParamContext("withDoubleCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty().allMatch { it is Double }
    }

    @Test
    fun `resolveParameter {Double nested list}`() {
        prepareParamContext("withDoubleNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch { it is Set<*> && it.all { b -> b is Double } }
    }

    @Test
    fun `resolveParameter {String}`() {
        prepareParamContext("withString")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is String && it.matches(Regex("[A-Z0-9]{42}")) }
    }

    @Test
    fun `resolveParameter {String list}`() {
        prepareParamContext("withStringList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty().allMatch { it is String }
    }

    @Test
    fun `resolveParameter {String set}`() {
        prepareParamContext("withStringSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty().allMatch { it is String }
    }

    @Test
    fun `resolveParameter {String collection}`() {
        prepareParamContext("withStringCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty().allMatch { it is String }
    }

    @Test
    fun `resolveParameter {String nested list}`() {
        prepareParamContext("withStringNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch { it is Set<*> && it.all { b -> b is String } }
    }

    @Test
    fun `resolveParameter {StringRegex}`() {
        prepareParamContext("withStringRegex")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {StringRegex list}`() {
        prepareParamContext("withStringRegexList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty()
            .allMatch { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {StringRegex set}`() {
        prepareParamContext("withStringRegexSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty()
            .allMatch { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {StringRegex collection}`() {
        prepareParamContext("withStringRegexCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty()
            .allMatch { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {StringRegex nested list}`() {
        prepareParamContext("withStringRegexNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch {
            it is Set<*> && it.all { b -> b is String && b.matches(Regex("[abc]+")) }
        }
    }

    @Test
    fun `resolveParameter {Regex}`() {
        prepareParamContext("withRegex")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result).matches { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {Regex list}`() {
        prepareParamContext("withRegexList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).isNotEmpty()
            .allMatch { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {Regex set}`() {
        prepareParamContext("withRegexSet")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Set<*>).isNotEmpty()
            .allMatch { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {Regex collection}`() {
        prepareParamContext("withRegexCollection")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as Collection<*>).isNotEmpty()
            .allMatch { it is String && it.matches(Regex("[abc]+")) }
    }

    @Test
    fun `resolveParameter {Regex nested list}`() {
        prepareParamContext("withRegexNestedList")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)

        assertThat(result as List<*>).allMatch {
            it is Set<*> && it.all { b -> b is String && b.matches(Regex("[abc]+")) }
        }
    }

    // endregion

    // region ParameterResolver.resolveParameter (failing)

    @Test
    fun `resolveParameter Fails on unknown parameterized type`() {
        prepareParamContext("withParameterized")

        assertThrows<ForgeryFactoryMissingException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on BoolForgery param invalid probability lt 0`() {
        prepareParamContext("withBoolInvalid1")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on BoolForgery param invalid probability gt 1`() {
        prepareParamContext("withBoolInvalid2")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on IntForgery param invalid min + stDev`() {
        prepareParamContext("withIntInvalid1")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on IntForgery param invalid max + stDev`() {
        prepareParamContext("withIntInvalid2")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on IntForgery param invalid min + mean`() {
        prepareParamContext("withIntInvalid3")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on IntForgery param invalid max + mean`() {
        prepareParamContext("withIntInvalid4")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on LongForgery param invalid min + stDev`() {
        prepareParamContext("withLongInvalid1")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on LongForgery param invalid max + stDev`() {
        prepareParamContext("withLongInvalid2")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on LongForgery param invalid min + mean`() {
        prepareParamContext("withLongInvalid3")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on LongForgery param invalid max + mean`() {
        prepareParamContext("withLongInvalid4")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on FloatForgery param invalid min + stDev`() {
        prepareParamContext("withFloatInvalid1")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on FloatForgery param invalid max + stDev`() {
        prepareParamContext("withFloatInvalid2")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on FloatForgery param invalid min + mean`() {
        prepareParamContext("withFloatInvalid3")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on FloatForgery param invalid max + mean`() {
        prepareParamContext("withFloatInvalid4")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on DoubleForgery param invalid min + stDev`() {
        prepareParamContext("withDoubleInvalid1")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on DoubleForgery param invalid max + stDev`() {
        prepareParamContext("withDoubleInvalid2")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on DoubleForgery param invalid min + mean`() {
        prepareParamContext("withDoubleInvalid3")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    @Test
    fun `resolveParameter Fails on DoubleForgery param invalid max + mean`() {
        prepareParamContext("withDoubleInvalid4")
        assertThrows<IllegalStateException> {
            testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        }
    }

    // endregion

    // region TestExecutionExceptionHandler

    @Test
    fun handleException() {
        whenever(mockExtensionContext.requiredTestInstance) doReturn mockTarget
        whenever(mockExtensionContext.requiredTestMethod) doReturn fakeMethod
        val thrown: Throwable = IOException("Whatever")

        val caught: Throwable? = try {
            testedExtension.handleTestExecutionException(
                mockExtensionContext,
                thrown
            )
            null
        } catch (e: IOException) {
            e
        }

        assertThat(caught).isSameAs(thrown)

        val forge = testedExtension.instanceForge
        assertThat(errStreamContent.toString())
            .isEqualTo(
                "<${mockTarget.javaClass.simpleName}.${fakeMethod.name}()> failed " +
                        "with Forge seed 0x${forge.seed.toString(16)}L\n" +
                        "Add the following @ForgeConfiguration annotation to your test class :\n" +
                        "\n" +
                        "\t@ForgeConfiguration(seed = 0x${forge.seed.toString(16)}L)\n" +
                        "\n"
            )
    }

    @Test
    fun `handleException with verbose message parameter`() {
        prepareParamContext("withInt")
        whenever(mockExtensionContext.requiredTestInstance) doReturn mockTarget
        val thrown: Throwable = IOException("Whatever")

        val result = testedExtension.resolveParameter(mockParameterContext, mockExtensionContext)
        val caught: Throwable? = try {
            testedExtension.handleTestExecutionException(
                mockExtensionContext,
                thrown
            )
            null
        } catch (e: IOException) {
            e
        }

        assertThat(caught).isSameAs(thrown)

        val forge = testedExtension.instanceForge
        assertThat(errStreamContent.toString())
            .isEqualTo(
                "<${mockTarget.javaClass.simpleName}.withInt()> failed " +
                        "with Forge seed 0x${forge.seed.toString(16)}L and:\n" +
                        "\t- param withInt::arg0 = $result\n\n" +
                        "Add the following @ForgeConfiguration annotation to your test class :\n" +
                        "\n" +
                        "\t@ForgeConfiguration(seed = 0x${forge.seed.toString(16)}L)\n" +
                        "\n"
            )
    }

    @Test
    fun `handleException with verbose message property`() {
        val target = KotlinReproducibilityTest()
        val testClass = target.javaClass
        val testMethod = testClass.getMethod("testRun1")
        whenever(mockExtensionContext.requiredTestInstance) doReturn target
        whenever(mockExtensionContext.requiredTestMethod) doReturn testMethod

        testedExtension.instanceForge.apply {
            addFactory(FooFactory())
            addFactory(BarFactory())
        }
        testedExtension.beforeEach(mockExtensionContext)
        val thrown: Throwable = IOException("Whatever")

        val caught: Throwable? = try {
            testedExtension.handleTestExecutionException(
                mockExtensionContext,
                thrown
            )
            null
        } catch (e: IOException) {
            e
        }

        assertThat(caught).isSameAs(thrown)

        val forge = testedExtension.instanceForge
        assertThat(errStreamContent.toString())
            .isEqualTo(
                "<KotlinReproducibilityTest.testRun1()> failed " +
                        "with Forge seed 0x${forge.seed.toString(16)}L and:\n" +
                        "\t- field KotlinReproducibilityTest::fakeBar = ${target.getBar()}\n" +
                        "\t- field KotlinReproducibilityTest::fakeFoo = ${target.getFoo()}\n\n" +
                        "Add the following @ForgeConfiguration annotation to your test class :\n" +
                        "\n" +
                        "\t@ForgeConfiguration(seed = 0x${forge.seed.toString(16)}L)\n" +
                        "\n"
            )
    }

    // endregion

    // region ExtensionContext Store

    @Test
    fun `stores the forge on beforeAll()`() {
        // Given
        val mockStore: ExtensionContext.Store = mock()
        whenever(mockExtensionContext.getStore(ExtensionContext.Namespace.GLOBAL)) doReturn mockStore

        // When
        testedExtension.beforeAll(mockExtensionContext)

        // Then
        verify(mockStore).put(
            ForgeExtension.EXTENSION_STORE_FORGE_KEY,
            testedExtension.instanceForge
        )
    }

    @Test
    fun `retrieves the stored forge`() {
        // Given
        val mockStore: ExtensionContext.Store = mock()
        whenever(mockExtensionContext.getStore(ExtensionContext.Namespace.GLOBAL)) doReturn mockStore
        whenever(mockStore.get(ForgeExtension.EXTENSION_STORE_FORGE_KEY)) doReturn testedExtension.instanceForge

        // When
        assertThat(ForgeExtension.getForge(mockExtensionContext))
            .isSameAs(testedExtension.instanceForge)
    }

    // endregion

    // region Internal

    fun prepareParamContext(methodName: String, parameterIndex: Int = 0) {
        val javaClass = Reflekta::class.java
        val method = javaClass.declaredMethods.first { it.name == methodName }
        val parameter = method.parameters[parameterIndex]
        mockParameterContext = MockParameterContext(javaClass, method, parameter)
        whenever(mockExtensionContext.requiredTestMethod) doReturn method
    }

    // endregion
}

@Suppress("unused", "UNUSED_PARAMETER")
internal class Reflekta(@Forgery s: String) {

    fun withStringForgery(@Forgery s: String) {
    }

    fun withUnannotatedString(s: String) {
    }

    fun withForge(f: Forge) {
    }

    // region boolean

    fun withBool(@BoolForgery b: Boolean) {
    }

    fun withNotBool(@BoolForgery s: String) {
    }

    fun withBoolInvalid1(@BoolForgery(probability = -1f) b: Boolean) {
    }

    fun withBoolInvalid2(@BoolForgery(probability = 2f) b: Boolean) {
    }

    fun withBoolList(@BoolForgery b: List<Boolean>) {
    }

    fun withBoolSet(@BoolForgery b: Set<Boolean>) {
    }

    fun withBoolCollection(@BoolForgery b: Collection<Boolean>) {
    }

    fun withBoolNestedList(@BoolForgery b: List<Set<Boolean>>) {
    }

    // endregion 

    // region int

    fun withInt(@IntForgery i: Int) {
    }

    fun withNotInt(@IntForgery s: String) {
    }

    fun withIntInvalid1(@IntForgery(min = 0, standardDeviation = 0) i: Int) {
    }

    fun withIntInvalid2(@IntForgery(max = 0, standardDeviation = 0) i: Int) {
    }

    fun withIntInvalid3(@IntForgery(min = 0, mean = 10) i: Int) {
    }

    fun withIntInvalid4(@IntForgery(max = 0, mean = 10) i: Int) {
    }

    fun withIntList(@IntForgery b: List<Int>) {
    }

    fun withIntSet(@IntForgery b: Set<Int>) {
    }

    fun withIntCollection(@IntForgery b: Collection<Int>) {
    }

    fun withIntNestedList(@IntForgery b: List<Set<Int>>) {
    }

    // endregion 

    // region long

    fun withLong(@LongForgery i: Long) {
    }

    fun withNotLong(@LongForgery s: String) {
    }

    fun withLongInvalid1(@LongForgery(min = 0L, standardDeviation = 0L) l: Long) {
    }

    fun withLongInvalid2(@LongForgery(max = 0L, standardDeviation = 0L) l: Long) {
    }

    fun withLongInvalid3(@LongForgery(min = 0L, mean = 10L) l: Long) {
    }

    fun withLongInvalid4(@LongForgery(max = 0L, mean = 10L) l: Long) {
    }

    fun withLongList(@LongForgery b: List<Long>) {
    }

    fun withLongSet(@LongForgery b: Set<Long>) {
    }

    fun withLongCollection(@LongForgery b: Collection<Long>) {
    }

    fun withLongNestedList(@LongForgery b: List<Set<Long>>) {
    }

    // endregion 

    // region float

    fun withFloat(@FloatForgery i: Float) {
    }

    fun withNotFloat(@FloatForgery s: String) {
    }

    fun withFloatInvalid1(@FloatForgery(min = 0f, standardDeviation = 0f) f: Float) {
    }

    fun withFloatInvalid2(@FloatForgery(max = 0f, standardDeviation = 0f) f: Float) {
    }

    fun withFloatInvalid3(@FloatForgery(min = 0f, mean = 10F) f: Float) {
    }

    fun withFloatInvalid4(@FloatForgery(max = 0f, mean = 10F) f: Float) {
    }

    fun withFloatList(@FloatForgery b: List<Float>) {
    }

    fun withFloatSet(@FloatForgery b: Set<Float>) {
    }

    fun withFloatCollection(@FloatForgery b: Collection<Float>) {
    }

    fun withFloatNestedList(@FloatForgery b: List<Set<Float>>) {
    }

    // endregion 

    // region double

    fun withDouble(@DoubleForgery i: Double) {
    }

    fun withNotDouble(@DoubleForgery s: String) {
    }

    fun withDoubleInvalid1(@DoubleForgery(min = 0.0, standardDeviation = 0.0) d: Double) {
    }

    fun withDoubleInvalid2(@DoubleForgery(max = 0.0, standardDeviation = 0.0) d: Double) {
    }

    fun withDoubleInvalid3(@DoubleForgery(min = 0.0, mean = 10.0) d: Double) {
    }

    fun withDoubleInvalid4(@DoubleForgery(max = 0.0, mean = 10.0) d: Double) {
    }

    fun withDoubleList(@DoubleForgery b: List<Double>) {
    }

    fun withDoubleSet(@DoubleForgery b: Set<Double>) {
    }

    fun withDoubleCollection(@DoubleForgery b: Collection<Double>) {
    }

    fun withDoubleNestedList(@DoubleForgery b: List<Set<Double>>) {
    }

    // endregion

    // region string

    fun withString(
        @StringForgery(
            StringForgeryType.HEXADECIMAL,
            Case.UPPER,
            42
        ) s: String
    ) {
    }

    fun withNotString(@StringForgery b: Boolean) {
    }

    fun withStringList(@StringForgery b: List<String>) {
    }

    fun withStringSet(@StringForgery b: Set<String>) {
    }

    fun withStringCollection(@StringForgery b: Collection<String>) {
    }

    fun withStringNestedList(@StringForgery b: List<Set<String>>) {
    }

    fun withStringRegex(@StringForgery(regex = "[abc]+") s: String) {
    }

    fun withNotStringRegex(@StringForgery(regex = "[abc]+") f: Float) {
    }

    fun withStringRegexList(@StringForgery(regex = "[abc]+") b: List<String>) {
    }

    fun withStringRegexSet(@StringForgery(regex = "[abc]+") b: Set<String>) {
    }

    fun withStringRegexCollection(@StringForgery(regex = "[abc]+") b: Collection<String>) {
    }

    fun withStringRegexNestedList(@StringForgery(regex = "[abc]+") b: List<Set<String>>) {
    }

    // endregion

    // region Regex

    fun withRegex(@StringForgery(regex = "[a-c]+") s: String) {
    }

    fun withNotRegex(@StringForgery(regex = "[a-c]+") f: Float) {
    }

    fun withRegexList(@StringForgery(regex = "[a-c]+") b: List<String>) {
    }

    fun withRegexSet(@StringForgery(regex = "[a-c]+") b: Set<String>) {
    }

    fun withRegexCollection(@StringForgery(regex = "[a-c]+") b: Collection<String>) {
    }

    fun withRegexNestedList(@StringForgery(regex = "[a-c]+") b: List<Set<String>>) {
    }

    // endregion

    fun withParameterized(@Forgery c: Comparator<Foo>) {
    }
}
