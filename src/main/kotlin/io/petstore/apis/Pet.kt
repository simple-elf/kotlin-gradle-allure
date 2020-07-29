package io.petstore.apis

import org.assertj.core.api.Assertions

class Pet {

    companion object {

        fun get(petId: Long): Pet.Companion {
            return this
        }

    }

}