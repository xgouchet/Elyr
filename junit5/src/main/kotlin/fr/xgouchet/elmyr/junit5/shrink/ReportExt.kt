package fr.xgouchet.elmyr.junit5.shrink

import fr.xgouchet.elmyr.junit5.ForgeTarget

@Suppress("UNCHECKED_CAST")
internal fun <T> List<InvocationReport>.reportForParam(
    paramName: String
): List<InjectionReport<T>> {
    return mapNotNull {
        val target = it.injectedData.firstOrNull { it.name == paramName && it.type == "param" }
        if (target != null) InjectionReport(
            target as ForgeTarget<T>,
            it.exception
        ) else null
    }
}

internal fun <T : Comparable<T>> List<InjectionReport<T>>.boundaries(
    boundary: (T, T) -> T
): List<T> {
    val boundaries = mutableListOf<T>()
    val sorted = this.sortedBy { it.target.value }
    val initial = sorted.first()
    sorted.drop(1).fold(initial) { acc, next ->
        val failureAppeared = acc.exception == null && next.exception != null
        val failureDisappeared = acc.exception != null && next.exception == null
        if (failureAppeared || failureDisappeared) {
            val candidate = boundary(acc.target.value, next.target.value)
            if (candidate != acc.target.value && candidate != next.target.value) {
                boundaries.add(candidate)
            }
        }
        next
    }
    return boundaries
}
