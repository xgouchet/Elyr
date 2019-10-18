package fr.xgouchet.elmyr.regex.node

import fr.xgouchet.elmyr.Forge
import fr.xgouchet.elmyr.regex.quantifier.Quantifier

internal open class SequenceNode(
    override var parentNode: ParentNode? = null
) : BaseParentNode() {

    // region ParentNode

    override fun handleQuantifier(quantifier: Quantifier) {
        if (children.isNotEmpty()) {
            val lastElement = children.removeAt(children.lastIndex)
            val newElement = QuantifiedNode(
                    node = lastElement,
                    quantifier = quantifier,
                    parentNode = this
            )
            children.add(newElement)
        }
    }

    // endregion

    // region QuantifiedNode

    override fun build(forge: Forge, builder: StringBuilder) {
        for (child in children) {
            child.build(forge, builder)
        }
    }

    // endregion
}