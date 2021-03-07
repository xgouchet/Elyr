package fr.xgouchet.elmyr.junit5.shrink

import fr.xgouchet.elmyr.junit5.ForgeTarget

internal data class InjectionReport<T>(
    val target: ForgeTarget<T>,
    val exception: Throwable?
)
