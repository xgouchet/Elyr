package fr.xgouchet.elmyr.inject.fixture

import fr.xgouchet.elmyr.annotation.Forgery

class KotlinInjectedChild :
    KotlinInjected() {

    @Deprecated("") lateinit var deprecatedField: String

    @Forgery lateinit var childFoo: Foo
}
