package fr.xgouchet.elmyr.junit5.shrink

annotation class Shrink(
    val maximumRunCount: Int = 1,
    val stopOnFirstError: Boolean = false
)
