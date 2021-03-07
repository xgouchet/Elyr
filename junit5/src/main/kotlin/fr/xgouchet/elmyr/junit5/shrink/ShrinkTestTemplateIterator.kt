package fr.xgouchet.elmyr.junit5.shrink

import fr.xgouchet.elmyr.junit5.ForgeTarget
import java.lang.AssertionError
import org.junit.jupiter.api.extension.TestTemplateInvocationContext

internal class ShrinkTestTemplateIterator(
    private val uniqueId: String,
    private val testName: String,
    maxRunCount: Int,
    private val reports: Map<String, List<InvocationReport>>
) : Iterator<TestTemplateInvocationContext> {

    private var index = 0
    private val intRange = 0 until maxRunCount

    // region Iterator

    override fun hasNext(): Boolean {
        if (index !in intRange) {
            val report = reports[uniqueId]
            if (report != null && report.any { it.exception != null }) {
                reportFailures(report)
            }
            return false
        }
        return true // reports[uniqueId]?.exception == null
    }

    override fun next(): TestTemplateInvocationContext {
        index++
        return ShrinkInvocationContext(testName)
    }

    // endregion

    // region Internal

    private fun reportFailures(invocationReports: List<InvocationReport>) {

        val paramTargets = invocationReports.first().injectedData.filter { it.type == "param" }

        val failingMessages = mutableListOf<String>()
        failingMessages.addAll(
            failingReports<Int>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingReports<Long>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingReports<Float>(paramTargets, invocationReports) { a, b -> a..b }
        )
        failingMessages.addAll(
            failingReports<Double>(paramTargets, invocationReports) { a, b -> a..b }
        )

        throw AssertionError(
            "Test ${invocationReports.first().contextDisplayName} failed\n" +
                    failingMessages.joinToString("\n")
        )
    }

    private inline fun <reified T : Comparable<T>> failingReports(
        targets: List<ForgeTarget<*>>,
        invocationReports: List<InvocationReport>,
        noinline rangeFactory: (T, T) -> ClosedRange<T>
    ): List<String> {
        return targets.filter { it.value is T }
            .map {
                val reports = invocationReports.reportForParam<T>(it.name)
                    .sortedBy { r -> r.target.value }
                val failingRanges = failingRanges(reports, rangeFactory)
                "- parameter ${it.name}: " +
                        failingRanges.joinToString { range ->
                            if (range.start == range.endInclusive) range.start.toString()
                            else "[${range.start}…${range.endInclusive}]"
                        }
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
}
