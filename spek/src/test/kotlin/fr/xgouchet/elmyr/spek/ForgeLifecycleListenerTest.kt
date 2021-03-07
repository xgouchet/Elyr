package fr.xgouchet.elmyr.spek

import com.nhaarman.mockitokotlin2.mock
import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType
import fr.xgouchet.elmyr.junit5.ForgeExtension
import fr.xgouchet.elmyr.spek.extension.SystemErrorStream
import fr.xgouchet.elmyr.spek.extension.SystemStreamExtension
import java.io.ByteArrayOutputStream
import java.util.Date
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import org.spekframework.spek2.lifecycle.ExecutionResult
import org.spekframework.spek2.lifecycle.GroupScope
import org.spekframework.spek2.lifecycle.TestScope

@Extensions(
    ExtendWith(MockitoExtension::class),
    ExtendWith(ForgeExtension::class),
    ExtendWith(SystemStreamExtension::class)
)
@MockitoSettings(strictness = Strictness.LENIENT)
internal class ForgeLifecycleListenerTest {

    class MockTestScope(
        val id: Any,
        override val parent: GroupScope
    ) : TestScope {
        constructor(
            name: String,
            parent: GroupScope
        ) : this(MockScopeId(name, "test"), parent)
    }

    class MockGroupScope(
        val id: Any,
        override val parent: GroupScope?
    ) : GroupScope {
        constructor(
            name: String,
            parent: GroupScope?
        ) : this(MockScopeId(name, "group"), parent)
    }

    class MockRootScope(
        val id: Any
    ) : GroupScope {
        constructor(
            name: String
        ) : this(MockScopeId(name, "class"))

        override val parent: GroupScope? = null
    }

    class MockScopeId(
        val name: String,
        val type: String
    )

    class MockId(
        val name: String
    )

    lateinit var testedForge: Forge

    lateinit var testedListener: ForgeLifecycleListener

    @StringForgery(regex = "([a-z]{2,5}\\.){4}")
    lateinit var fakeRootPackage: String

    @StringForgery(StringForgeryType.ALPHABETICAL)
    lateinit var fakeRootName: String

    @StringForgery(StringForgeryType.ALPHABETICAL)
    lateinit var fakeGroupName: String

    @StringForgery(StringForgeryType.ALPHABETICAL)
    lateinit var fakeTestName: String

    @LongForgery(0L, 0x7FFFFFFF)
    var fakeRootSeed: Long = 0L

    @LongForgery(0L, 0x7FFFFFFF)
    var fakeGroupSeed: Long = 0L

    @LongForgery(0L, 0x7FFFFFFF)
    var fakeTestSeed: Long = 0L

    @BeforeEach
    fun `set up`() {
        testedForge = Forge().apply { seed = 0L }
        testedListener = ForgeLifecycleListener(
            testedForge,
            mapOf(
                fakeRootName to fakeRootSeed,
                fakeGroupName to fakeGroupSeed,
                "$fakeGroupName/$fakeTestName" to fakeTestSeed
            )
        )
    }

    @Test
    fun `ignore group without id`(
        forge: Forge
    ) {
        // Given
        val group = mock<GroupScope>()

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isNotEqualTo(0L)
    }

    @Test
    fun `ignore group with invalid id`(
        forge: Forge
    ) {
        // Given
        val group = MockGroupScope(Date(), null)

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isNotEqualTo(0L)
    }

    @Test
    fun `reset seed on root group start`(
        forge: Forge
    ) {
        // Given
        val group = MockRootScope(forge.anAlphaNumericalString())

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isNotEqualTo(0L)
    }

    @Test
    fun `reset seed on group start`(
        forge: Forge
    ) {
        // Given
        val group = createMockGroup(forge)

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isNotEqualTo(0L)
    }

    @Test
    fun `reset seed on test start`(
        forge: Forge
    ) {
        // Given
        val test = createMockTest(forge)

        // When
        testedListener.beforeExecuteTest(test)

        // Then
        assertThat(testedForge.seed).isNotEqualTo(0L)
    }

    @Test
    fun `use provided seed on root group start`() {
        // Given
        val group = MockRootScope("$fakeRootPackage$fakeRootName")

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isEqualTo(fakeRootSeed)
    }

    @Test
    fun `use provided seed on group start`() {
        // Given
        val group = MockGroupScope(fakeGroupName, null)

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isEqualTo(fakeGroupSeed)
    }

    @Test
    fun `use provided seed on group start with untyped id`(
        forge: Forge
    ) {
        // Given
        val group = MockGroupScope(MockId(fakeGroupName), null)

        // When
        testedListener.beforeExecuteGroup(group)

        // Then
        assertThat(testedForge.seed).isEqualTo(fakeGroupSeed)
    }

    @Test
    fun `use provided seed on test start`() {
        // Given
        val test = MockTestScope(fakeTestName, MockGroupScope(fakeGroupName, null))

        // When
        testedListener.beforeExecuteTest(test)

        // Then
        assertThat(testedForge.seed).isEqualTo(fakeTestSeed)
    }

    @Test
    fun `print error message with seeds on failure`(
        forge: Forge,
        @SystemErrorStream errorStream: ByteArrayOutputStream
    ) {
        // Given
        val exception = RuntimeException(forge.anAlphabeticalString())
        val result = ExecutionResult.Failure(exception)
        val test = MockTestScope(fakeTestName, MockGroupScope(fakeGroupName, null))

        // When
        testedListener.beforeExecuteTest(test)
        testedListener.afterExecuteTest(test, result)

        // Then
        assertThat(testedForge.seed).isEqualTo(fakeTestSeed)
        val name = "$fakeGroupName/$fakeTestName"
        val seedHex = "0x${fakeTestSeed.toString(16)}L"
        assertThat(errorStream.toString()).isEqualTo(
            "<$name> failed with Forge seed $seedHex\n" +
                "Add the following seeds to your Spek class :\n\n" +
                "\tval forge = spekForge(\n" +
                "\t\tmapOf(\n" +
                "\t\t\t\"$name\" to $seedHex\n" +
                "\t\t)\n" +
                "\t)\n"
        )
    }

    // region Internal

    private fun createMockTest(forge: Forge): TestScope {
        return MockTestScope(forge.anAlphabeticalString(), createMockGroup(forge))
    }

    private fun createMockGroup(forge: Forge): GroupScope {
        val parent = forge.aNullable { MockGroupScope(anAlphabeticalString(), null) }

        return MockGroupScope(forge.anAlphabeticalString(), parent)
    }

    // endregion
}
