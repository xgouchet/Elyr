package fr.xgouchet.elmyr.junit5.shrink

import fr.xgouchet.elmyr.junit5.ForgeTarget

internal data class InvocationReport(
    val contextUniqueId: String,
    val contextDisplayName: String,
    val injectedData: List<ForgeTarget<*>> = emptyList(),
    val exception: Throwable? = null
)
