package io.petstore

import io.qameta.allure.Allure
import io.qameta.allure.Allure.step
import io.qameta.allure.Step

object AllureHelpers {

    //@Step("{message}")
    fun printStep(message: Any?) {
        step(message.toString())
        println(message)
    }

    fun Step(name: String, block: () -> Unit) {
        step(name, block)
    }

    fun Step(name: String) {
        step(name)
    }

    fun <T> actionStep(description: String, action: (String) -> T): T = Allure.step(description, Allure.ThrowableRunnable {
        action(description)
    })

    fun <T> actionStep(description: String) {
        Allure.step(description)
    }

}