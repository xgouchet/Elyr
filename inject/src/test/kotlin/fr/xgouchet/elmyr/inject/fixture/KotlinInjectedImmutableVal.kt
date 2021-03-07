package fr.xgouchet.elmyr.inject.fixture

import fr.xgouchet.elmyr.annotation.BoolForgery
import fr.xgouchet.elmyr.annotation.DoubleForgery
import fr.xgouchet.elmyr.annotation.FloatForgery
import fr.xgouchet.elmyr.annotation.Forgery
import fr.xgouchet.elmyr.annotation.IntForgery
import fr.xgouchet.elmyr.annotation.LongForgery
import fr.xgouchet.elmyr.annotation.StringForgery
import fr.xgouchet.elmyr.annotation.StringForgeryType

class KotlinInjectedImmutableVal {

    @Forgery
    val finalFoo42: Foo = Foo(42)

    @BoolForgery
    val finalBool: Boolean = true

    @IntForgery
    val finalInt: Int = 42

    @LongForgery
    val finalLong: Long = 42L

    @FloatForgery
    val finalFloat: Float = 42f

    @DoubleForgery
    val finalDouble: Double = 42.0

    @StringForgery(StringForgeryType.ALPHA_NUMERICAL)
    val finalString: String = "spam"

    @StringForgery(regex = ".*")
    val finalRegex: String = "foo"
}
