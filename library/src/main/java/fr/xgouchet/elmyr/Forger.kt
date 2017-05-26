package fr.xgouchet.elmyr

import fr.xgouchet.elmyr.regex.RegexBuilder
import java.lang.Integer.min
import java.lang.Math.round


/**
 * @author Xavier F. Gouchet
 */
open class Forger {

    internal val rng = java.util.Random()

    /**
     * Resets this forger with the given seed. Knowing the seed allow the forger to reproduce
     * previous data.
     *
     * @param seed the seed to use (try and remember to be able to reproduce a forgery)
     */
    fun reset(seed: Long) {
        rng.setSeed(seed)
    }

    // region Bool

    /**
     * @param probability the probability the boolean will be true (default 0.5f)
     * @return a boolean
     */
    @JvmOverloads
    fun aBool(probability: Float = 0.5f): Boolean {
        return rng.nextFloat() < probability
    }

    // endregion

    // region Int

    /**
     * @param constraint a constraint on the int to forge
     * @return an int between constraint and max
     */
    fun anInt(constraint: IntConstraint): Int {
        when (constraint) {
            IntConstraint.ANY -> return anInt()
            IntConstraint.TINY -> return aTinyInt()
            IntConstraint.SMALL -> return aSmallInt()
            IntConstraint.BIG -> return aBigInt()
            IntConstraint.HUGE -> return aHugeInt()
            IntConstraint.POSITIVE -> return aPositiveInt()
            IntConstraint.POSITIVE_STRICT -> return aPositiveInt(strict = true)
            IntConstraint.NEGATIVE -> return aNegativeInt()
            IntConstraint.NEGATIVE_STRICT -> return aNegativeInt(strict = true)
        }
    }

    /**
     * @param min the minimum value (inclusive), default = Int#MIN_VALUE
     * @param max the maximum value (exclusive), default = Int#MAX_VALUE
     * @return an int between min and max
     */
    @JvmOverloads
    fun anInt(min: Int = Int.MIN_VALUE, max: Int = Int.MAX_VALUE): Int {

        if (min >= max) {
            throw IllegalArgumentException("The ‘min’ boundary ($min) of the range should be less than the ‘max’ boundary ($max)")
        }

        val range = max.toLong() - min.toLong()
        val rn = (Math.abs(rng.nextLong()) % range) + min

        return (rn and 0xFFFFFFFFL).toInt()
    }

    /**
     * @param strict if true, then it will return a non 0 int (default : false)
     * @return a positive int
     */
    @JvmOverloads
    fun aPositiveInt(strict: Boolean = false): Int {
        return anInt(min = if (strict) 1 else 0)
    }

    /**
     * @param strict if true, then it will return a non 0 int (default : true)
     * @return a negative int
     */
    @JvmOverloads
    fun aNegativeInt(strict: Boolean = true): Int {
        return anInt(min = Int.MIN_VALUE, max = if (strict) -1 else 0)
    }

    /**
     * @return a strictly positive int, less than #TINY_THRESHOLD
     */
    fun aTinyInt(): Int {
        return anInt(1, TINY_THRESHOLD)
    }

    /**
     * @return a strictly positive int, less than  #SMALL_THRESHOLD
     */
    fun aSmallInt(): Int {
        return anInt(1, SMALL_THRESHOLD)
    }

    /**
     * @return a strictly positive int, greater than #BIG_THRESHOLD
     */
    fun aBigInt(): Int {
        return anInt(BIG_THRESHOLD)
    }

    /**
     * @return a strictly positive int, greater than #HUGE_THRESHOLD
     */
    fun aHugeInt(): Int {
        return anInt(HUGE_THRESHOLD)
    }

    /**
     * @param mean the mean value of the distribution (default : 0)
     * @param standardDeviation the standard deviation value of the distribution (default : 100)
     * @return an int picked from a gaussian distribution (aka bell curve)
     */
    @JvmOverloads
    fun aGaussianInt(mean: Int = 0, standardDeviation: Int = 100): Int {
        if (standardDeviation < 0) {
            throw IllegalArgumentException("Standard deviation ($standardDeviation) must be a positive (or null) value")
        } else if (standardDeviation == 0) {
            return mean
        } else {
            return round((rng.nextGaussian() * standardDeviation)).toInt() + mean
        }
    }

    // endregion

    // region Float

    /**
     * @param constraint a constraint on the int to forge
     * @return an int between constraint and max
     */
    fun aFloat(constraint: FloatConstraint): Float {
        when (constraint) {
            FloatConstraint.ANY -> return aFloat()
            FloatConstraint.POSITIVE -> return aPositiveFloat()
            FloatConstraint.POSITIVE_STRICT -> return aPositiveFloat(strict = true)
            FloatConstraint.NEGATIVE -> return aNegativeFloat()
            FloatConstraint.NEGATIVE_STRICT -> return aNegativeFloat(strict = true)
        }
    }

    /**
     * @param min the minimum value (inclusive), default = -Float#MAX_VALUE
     * @param max the maximum value (exclusive), default = Float#MAX_VALUE
     * @return an int between min and max
     */
    @JvmOverloads
    fun aFloat(min: Float = -Float.MAX_VALUE, max: Float = Float.MAX_VALUE): Float {

        if (min > max) {
            throw IllegalArgumentException("The ‘min’ boundary ($min) of the range should be less than (or equal to) the ‘max’ boundary ($max)")
        }

        val range = max - min
        if (range == Float.POSITIVE_INFINITY) {
            return (rng.nextFloat() - 0.5f) * Float.MAX_VALUE * 2
        } else {
            return (rng.nextFloat() * range) + min
        }
    }

    /**
     * @param strict if true, then it will return a non 0 int (default : false)
     * @return a positive int
     */
    @JvmOverloads
    fun aPositiveFloat(strict: Boolean = false): Float {
        return aFloat(min = if (strict) Float.MIN_VALUE else 0.0f)
    }

    /**
     * @param strict if true, then it will return a non 0 int (default : true)
     * @return a negative int
     */
    @JvmOverloads
    fun aNegativeFloat(strict: Boolean = true): Float {
        return -aPositiveFloat(strict)
    }

    /**
     * @param mean the mean value of the distribution (default : 0.0f)
     * @param standardDeviation the standard deviation value of the distribution (default : 1.0f)
     * @return a float picked from a gaussian distribution (aka bell curve)
     */
    @JvmOverloads
    fun aGaussianFloat(mean: Float = 0f, standardDeviation: Float = 1f): Float {
        if (standardDeviation < 0) {
            throw IllegalArgumentException("Standard deviation ($standardDeviation) must be a positive (or null) value")
        } else if (standardDeviation == 0f) {
            return mean
        } else {
            return (rng.nextGaussian().toFloat() * standardDeviation) + mean
        }
    }

    // endregion

    // region Char

    /**
     * @param constraint a constraint on the char to forge
     * @param case the case to use (depending on the constraint, it might be ignored)
     * @return a Char with the given constraints
     */
    @JvmOverloads
    fun aChar(constraint: CharConstraint,
              case: Case = Case.ANY): Char {
        when (constraint) {

            CharConstraint.ANY -> return aChar()
            CharConstraint.HEXADECIMAL -> return anHexadecimalChar(case)
            CharConstraint.ALPHA -> return anAlphabeticalChar(case)
            CharConstraint.ALPHA_NUM -> return anAlphaNumericalChar(case)
            CharConstraint.NUMERICAL -> return aNumericalChar()
            CharConstraint.WHITESPACE -> return aWhitespaceChar()
            CharConstraint.NON_HEXADECIMAL -> return aNonHexadecimalChar()
            CharConstraint.NON_ALPHA -> return aNonAlphabeticalChar()
            CharConstraint.NON_ALPHA_NUM -> return aNonAlphaNumericalChar()
            CharConstraint.NON_NUMERICAL -> return aNonNumericalChar()
            CharConstraint.NON_WHITESPACE -> return aNonWhitespaceChar()

            else -> TODO("Unknown constraint !")
        }
    }

    /**
     * @param min the min char code to use (inclusive, default = 0x20 == space)
     * @param max the max char code to use (exclusive, default = 0xD800)
     * @return a Char within the given range
     */
    @JvmOverloads
    fun aChar(min: Char = MIN_PRINTABLE, max: Char = MAX_UTF8): Char {
        return anInt(min.toInt(), max.toInt()).toChar()
    }

    /**
     * @return a Char within the standard ASCII printable characters
     */
    fun anAsciiChar(): Char {
        return aChar(MIN_PRINTABLE, MAX_ASCII)
    }

    /**
     * @return a Char within the extended ASCII printable characters
     */
    fun anExtendedAsciiChar(): Char {
        return aChar(MIN_PRINTABLE, MAX_ASCII_EXTENDED)
    }

    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER and Case.ANY, anything else falls back to Case.ANY)
     * @return an alpha character (from the roman alphabet), in the given case
     */
    @JvmOverloads
    fun anAlphabeticalChar(case: Case = Case.ANY): Char {
        when (case) {
            Case.UPPER -> return anElementFrom(ALPHA_UPPER)
            Case.LOWER -> return anElementFrom(ALPHA_LOWER)
            else -> return anElementFrom(ALPHA)
        }
    }

    /**
     * @return a character which is not alphabetical
     */
    fun aNonAlphabeticalChar(): Char {
        var res: Char
        do {
            res = aChar(CharConstraint.ANY, Case.ANY)
        } while (ALPHA.contains(res))
        return res
    }


    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER and Case.ANY, anything else falls back to Case.ANY)
     * @return a standard vowel character (‘a’, ‘e’, ‘i’, ‘o’, ‘u’, ‘y’), in the given case
     */
    @JvmOverloads
    fun aVowelChar(case: Case = Case.ANY): Char {
        when (case) {
            Case.UPPER -> return anElementFrom(VOWEL_UPPER)
            Case.LOWER -> return anElementFrom(VOWEL_LOWER)
            else -> return anElementFrom(VOWEL)
        }
    }


    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER and Case.ANY, anything else falls back to Case.ANY)
     * @return a standard consonant character (any roman alphabet except ‘a’, ‘e’, ‘i’, ‘o’, ‘u’, ‘y’), in the given case
     */
    @JvmOverloads
    fun aConsonantChar(case: Case = Case.ANY): Char {
        when (case) {
            Case.UPPER -> return anElementFrom(CONSONANT_UPPER)
            Case.LOWER -> return anElementFrom(CONSONANT_LOWER)
            else -> return anElementFrom(CONSONANT)
        }
    }

    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER and Case.ANY, anything else falls back to Case.ANY)
     * @return an alphabetical or digit character, in the given case
     */
    @JvmOverloads
    fun anAlphaNumericalChar(case: Case = Case.ANY): Char {
        when (case) {
            Case.UPPER -> return anElementFrom(ALPHA_NUM_UPPER)
            Case.LOWER -> return anElementFrom(ALPHA_NUM_LOWER)
            else -> return anElementFrom(ALPHA_NUM)
        }
    }

    /**
     * @return a character neither alphabetical nor numeric
     */
    fun aNonAlphaNumericalChar(): Char {
        var res: Char
        do {
            res = aChar(CharConstraint.ANY, Case.ANY)
        } while (ALPHA_NUM.contains(res))
        return res
    }

    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER , anything else falls back to Case.LOWER)
     * @return a digit (0 to F)
     */
    @JvmOverloads
    fun anHexadecimalChar(case: Case = Case.LOWER): Char {
        when (case) {
            Case.UPPER -> return anElementFrom(HEXA_UPPER)
            else -> return anElementFrom(HEXA_LOWER)
        }
    }

    /**
     * @return a character that is not an hexadecimal digit
     */
    fun aNonHexadecimalChar(): Char {
        var res: Char
        do {
            res = aChar(CharConstraint.ANY, Case.ANY)
        } while (HEXA_LOWER.contains(res) or HEXA_UPPER.contains(res))
        return res
    }

    /**
     * @return a numerical (0 to 9)
     */
    fun aNumericalChar(): Char {
        return anElementFrom(DIGIT)
    }

    /**
     * a non numerical character
     */
    fun aNonNumericalChar(): Char {
        var res: Char
        do {
            res = aChar(CharConstraint.ANY, Case.ANY)
        } while (DIGIT.contains(res))
        return res
    }

    /**
     * @return a whitespace character
     */
    fun aWhitespaceChar(): Char {
        return anElementFrom(WHITESPACE)
    }

    /**
     * @return a non whitespace characer
     */
    fun aNonWhitespaceChar(): Char {
        var res: Char
        do {
            res = aChar(CharConstraint.ANY, Case.ANY)
        } while (WHITESPACE.contains(res))
        return res
    }
    // endregion

    // region String

    /**
     * @param constraint the constraint to use (default : none)
     * @param case the case to use (ignored when constraint is Any)
     * @param size the size of the string (or -1 for a random sized String)
     * @return a truly random string (with chars in the whole UTF-8 spectrum)
     */
    @JvmOverloads
    fun aString(constraint: StringConstraint = StringConstraint.ANY,
                case: Case = Case.ANY,
                size: Int = -1): String {
        when (constraint) {
            StringConstraint.ANY -> return String(CharArray(getWordSize(size), { aChar(CharConstraint.ANY, Case.ANY) }))
            StringConstraint.WORD -> return aWord(case, size)
            StringConstraint.LIPSUM -> return aSentence(case, size)
            StringConstraint.HEXADECIMAL -> return anHexadecimalString(case, size)
            StringConstraint.URL -> return aUrl()
            StringConstraint.EMAIL -> return anEmail()
        }
    }

    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER, Case.CAPITALIZE or Case.ANY)
     * @param size the size of the string (or -1 for a random sized String)
     * @return a String that kind of look like a word
     */
    @JvmOverloads
    fun aWord(case: Case = Case.ANY, size: Int = -1): String {
        var consonant: Boolean = aBool()
        val resultSize = getWordSize(size)

        val array = CharArray(resultSize)
        var currentCase = case
        for (i in 0 until resultSize) {
            if (case == Case.CAPITALIZE) {
                currentCase = if (i == 0) Case.UPPER else Case.LOWER
            }
            array[i] = if (consonant) aConsonantChar(currentCase) else aVowelChar(currentCase)
            consonant = !consonant
        }

        return String(array)
    }

    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER, Case.CAPITALIZE, Case.CAPITALIZED_SENTENCE or Case.ANY)
     * @param size the size of the string (or -1 for a random sized String). Note that to construct a good sentence, the
     * size should be at least 3 characters long
     *
     * @return a String that kind of look like a sentence (think Lorem Ipsum)
     */
    @JvmOverloads
    fun aSentence(case: Case = Case.ANY, size: Int = -1): String {
        val resultSize: Int = if (size > 0) size else (aSmallInt() + 4)

        // The only way to have a punctuated sentende. Kind of
        if (resultSize == 1) return "‽"

        val builder = StringBuilder()

        while (builder.length < resultSize) {
            val actualCase: Case
            if (case == Case.CAPITALIZED_SENTENCE) {
                actualCase = if (builder.isEmpty()) Case.CAPITALIZE else Case.LOWER
            } else {
                actualCase = case
            }
            val remainingSize = resultSize - builder.length

            if (remainingSize < 7) {
                builder.append(aWord(actualCase, remainingSize - 1))
                builder.append(".") // TODO maybe randomize the punctuation ?
            } else {
                val wordSize = min(anInt(2, 10), remainingSize - 5)
                builder.append(aWord(actualCase, wordSize))
                builder.append(" ")
            }
        }

        return builder.toString()
    }

    /**
     * @param case the case to use (supports Case.UPPER, Case.LOWER , anything else falls back to Case.LOWER)
     * @param size the size of the string (or -1 for a random sized String)
     * @return an hexadecimal string
     */
    @JvmOverloads
    fun anHexadecimalString(case: Case = Case.LOWER, size: Int = -1): String {
        val resultSize = getWordSize(size)
        return String(CharArray(resultSize, { anHexadecimalChar(case) }))
    }

    /**
     * @param regex a regular expression to drive the generation. Note that parsing the regex can take some time depending
     * on the regex complexity. Also not all regex feature are supported.
     *
     * @return a String matching the given regular expression
     */
    fun aStringMatching(regex: String): String {
        return RegexBuilder(regex).buildString(this)
    }

    /**
     * @param regex a regular expression to drive the generation. Note that parsing the regex can take some time depending
     * on the regex complexity. Also not all regex feature are supported.
     *
     * @return a String matching the given regular expression
     */
    fun aStringMatching(regex: Regex): String {
        return aStringMatching(regex.pattern)
    }

    /**
     * @return a String matching a standard URL format
     */
    fun aUrl(): String {
        val builder = StringBuilder()

        // scheme
        builder.append(aWord(Case.LOWER, anInt(2, 7)))
                .append("://")

        // host (subdomain.domain.tld)
        builder.append(aWord(Case.LOWER, anInt(3, 7)))
                .append('.')
                .append(aWord(Case.LOWER, anInt(5, 11)))
                .append('.')
                .append(aWord(Case.LOWER, 3))
                .append('/')

        // path segments
        if (aBool()) {
            val pathSegmentCount = aTinyInt()
            for (i in 0 until pathSegmentCount) {
                builder.append(aWord(Case.ANY, anInt(2, 13)))
                        .append('/')
            }
        } else {
            // an article blurb
            builder.append(aWord(Case.CAPITALIZE, anInt(2, 13)))
            val wordsCount = aTinyInt()
            for (i in 0 until wordsCount) {
                builder.append('-')
                        .append(aWord(Case.LOWER, anInt(2, 7)))
            }
        }

        // anchor ?
        if (aBool()) {
            builder.append('#')
                    .append(aWord())
        }

        // query params
        if (aBool()) {
            val queryParamsCount = aTinyInt()
            for (i in 0 until queryParamsCount) {
                builder.append(if (i == 0) '?' else '&')
                        .append(aWord(Case.ANY, anInt(2, 7)))
                        .append('=')
                        .append(aWord(Case.ANY, anInt(3, 13)))
            }
        }


        return builder.toString()
    }

    /**
     * @return a String matching a standard URL format
     */
    fun anEmail(): String {
        val builder = StringBuilder()

        // username
        builder.append(aWord(Case.CAPITALIZE, anInt(3, 11)))
                .append(anElementFrom('_', '.', '-'))
                .append(aWord(Case.CAPITALIZE, anInt(3, 11)))
                .append(aTinyInt())

        // category ?
        if (aBool()) {
            builder.append('+')
                    .append(aWord(Case.LOWER))
        }

        builder.append('@')

        // host (subdomain.domain.tld)
        builder.append(aWord(Case.LOWER, anInt(3, 7)))
                .append('.')
                .append(aWord(Case.LOWER, anInt(5, 11)))
                .append('.')
                .append(aWord(Case.LOWER, 3))

        return builder.toString()
    }

    private fun getWordSize(size: Int): Int {
        return if (size > 0) size else aTinyInt()
    }

    // endregion

    // region Collections

    /**
     * @param set a Set
     * @return an element “randomly” picked in the set
     */
    fun <K, V> anElementFrom(map: Map<K, V>): Map.Entry<K, V> {
        val index = anInt(0, map.size)
        return map.entries.elementAt(index)
    }

    /**
     * @param set a Set
     * @return an element “randomly” picked in the set
     */
    fun <T> anElementFrom(set: Set<T>): T {
        val index = anInt(0, set.size)
        return set.elementAt(index)
    }

    /**
     * @param list a List
     * @return an element “randomly” picked in the list
     */
    fun <T> anElementFrom(list: List<T>): T {
        val index = anInt(0, list.size)
        return list[index]
    }

    /**
     * @param array an Array
     * @return an element “randomly” picked in the array
     */
    fun <T> anElementFrom(vararg elements: T): T {
        val index = anInt(0, elements.size)
        return elements[index]
    }

    /**
     * @param array an Array
     * @return an element “randomly” picked in the array
     */
    fun anElementFrom(array: BooleanArray): Boolean {
        val index = anInt(0, array.size)
        return array[index]
    }

    /**
     * @param array an Array
     * @return an element “randomly” picked in the array
     */
    fun anElementFrom(array: CharArray): Char {
        val index = anInt(0, array.size)
        return array[index]
    }

    /**
     * @param array an Array
     * @return an element “randomly” picked in the array
     */
    fun anElementFrom(array: IntArray): Int {
        val index = anInt(0, array.size)
        return array[index]
    }

    /**
     * @param array an Array
     * @return an element “randomly” picked in the array
     */
    fun anElementFrom(array: FloatArray): Float {
        val index = anInt(0, array.size)
        return array[index]
    }

    // endregion

    // region Enum

    /**
     * @param enumClass an Enum class
     * @return an element “randomly” picked in the enum values
     */
    fun <E : Enum<E>> aValueFrom(enumClass: Class<E>): E {
        return anElementFrom(*enumClass.enumConstants)
    }

    // endregion

    companion object {

        // Int
        val TINY_THRESHOLD = 0x20
        val SMALL_THRESHOLD = 0x100
        val BIG_THRESHOLD = 0x10000
        val HUGE_THRESHOLD = 0x1000000

        // Char
        internal val MIN_PRINTABLE = 0x20.toChar()
        internal val MAX_ASCII = 0x7F.toChar()
        internal val MAX_ASCII_EXTENDED = 0xFF.toChar()
        internal val MAX_UTF8 = 0xD800.toChar()

        internal val ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray()
        internal val ALPHA_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
        internal val ALPHA_LOWER = "abcdefghijklmnopqrstuvwxyz".toCharArray()

        internal val ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_0123456789".toCharArray()
        internal val ALPHA_NUM_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789".toCharArray()
        internal val ALPHA_NUM_LOWER = "abcdefghijklmnopqrstuvwxyz_0123456789".toCharArray()

        internal val HEXA_UPPER = "ABCDEF0123456789".toCharArray()
        internal val HEXA_LOWER = "abcdef0123456789".toCharArray()

        internal val VOWEL = "aeiouyAEIOUY".toCharArray()
        internal val VOWEL_UPPER = "AEIOUY".toCharArray()

        internal val VOWEL_LOWER = "aeiouy".toCharArray()
        internal val CONSONANT = "ZRTPQSDFGHJKLMWXCVBNzrtpqsdfghjklmwxcvbn".toCharArray()
        internal val CONSONANT_UPPER = "ZRTPQSDFGHJKLMWXCVBN".toCharArray()

        internal val CONSONANT_LOWER = "zrtpqsdfghjklmwxcvbn".toCharArray()

        internal val DIGIT = "0123456789".toCharArray()

        internal val WHITESPACE = "\t\n\r ".toCharArray()
    }


}