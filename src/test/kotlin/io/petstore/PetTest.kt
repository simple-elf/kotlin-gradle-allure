package io.petstore

import io.petstore.apis.PetApi
import io.petstore.models.PetModel
import io.petstore.models.PetModel.*
import io.petstore.AllureHelpers.printStep
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertAll

@DisplayName("Pet")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PetTest : TestBase() {

    @Test
    fun `Get pet by ID`() {
        //printStep("GET /pet/1")
        val pet: PetModel = PetApi.get(1)
        printStep(pet)
        //Step(pet.toString())
        assertAll("pet",
            { assertEquals(1, pet.id, pet::id.name) },
            { assertEquals("Cat 1", pet.name, pet::name.name) },
            { assertEquals("available", pet.status, pet::status.name) },
            { assertEquals(Category(2, "Cats"), pet.category, pet::category.name) },
            { assertEquals(listOf("url1", "url2"), pet.photoUrls, pet::photoUrls.name) },
            { assertEquals(listOf(Tag(1, "tag1"), Tag(2, "tag2")), pet.tags, pet::tags.name) }
        )
    }

    fun test2() {
        assertAll(
            { assertTrue(false) },
            { assertTrue(false, "must be true") },
            { assertFalse(true) },
            { assertFalse(true, "must be false") },
            { assertNull("!null") },
            { assertNull("!null", "must be null") },
            { assertNotNull(null) },
            { assertNotNull(null, "must be null") },
            { assertEquals(1, 2) },
            { assertEquals(1, 2, "must be equals") },
            { assertNotEquals(1, 1) },
            { assertNotEquals(1, 1, "must not equals") }
        )
    }

    @Test
    fun `Add new pet`() {
        val newPet =
            PetModel(
                name = "Kira",
                category = Category(1, "Dogs"),
                status = Status.Available.value
            )
        printStep(newPet)

        val petId = PetApi.add(newPet).id
        printStep("New pet id: $petId")
        val pet = PetApi.get(petId)
        printStep(pet)
        assertAll("new pet",
            { assertEquals(petId, pet.id, pet::id.name) },
            { assertEquals(newPet.name, pet.name, pet::name.name) },
            { assertEquals(newPet.status, pet.status, pet::status.name) },
            { assertEquals(newPet.category, pet.category, pet::category.name) }
        )

        PetApi.delete(petId)
    }

}