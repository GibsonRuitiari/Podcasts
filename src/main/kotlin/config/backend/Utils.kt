@file:OptIn(ExperimentalContracts::class)
package config.backend

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

// smart cast the variable of type T so we don't have to do this T!!
@OptIn(ExperimentalContracts::class)
fun <T> T?.isNull(): Boolean {
    contract {
        returns(false) implies (this@isNull != null)
    }
    return this == null
}