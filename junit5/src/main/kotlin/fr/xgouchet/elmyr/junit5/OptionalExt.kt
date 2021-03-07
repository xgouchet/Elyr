package fr.xgouchet.elmyr.junit5

import java.util.Optional

fun <T : Any> Optional<T>.orNull(): T? {
    return if (isPresent) get() else null
}
