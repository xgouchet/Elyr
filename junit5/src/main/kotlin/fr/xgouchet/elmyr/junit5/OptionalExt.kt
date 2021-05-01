package fr.xgouchet.elmyr.junit5

import java.util.Optional

internal fun <T : Any> Optional<T>.orNull(): T? {
    return if (isPresent) get() else null
}
