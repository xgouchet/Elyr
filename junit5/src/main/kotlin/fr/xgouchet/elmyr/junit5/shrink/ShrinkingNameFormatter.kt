package fr.xgouchet.elmyr.junit5.shrink

class ShrinkingNameFormatter(
    val displayName: String
) {

    fun format(currentRun: Int, maxRun: Int): String {
        return if (currentRun > 1 && maxRun > 0) {
            "$displayName [$currentRun/$maxRun]"
        } else {
            this.displayName
        }
    }
}
