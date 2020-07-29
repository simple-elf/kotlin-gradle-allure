package io.petstore.apis

import io.qameta.allure.Step
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.response.Response
import io.petstore.models.PetModel
import io.restassured.response.ValidatableResponse
import org.hamcrest.CoreMatchers.equalTo

object PetApi {
    lateinit var response: Response
    lateinit var validatableResponse: ValidatableResponse
    lateinit var pet: PetModel

    @Step("Add pet")
    fun add(petModel: PetModel): PetModel {
        pet = Given {
            body(petModel)
        } When {
            post("/pet")
        } Then {
            statusCode(200)
        } Extract {
            `as`(PetModel::class.java)
        }
        return pet
    }

    @Step("GET /pet/{petId}")
    fun get(petId: Long): PetModel {
        pet = Given {
            given()
        } When {
            //step("GET_2 /pet/" + petId)
            get("/pet/$petId")
        } Then {
            statusCode(200)
        } Extract {
            `as`(PetModel::class.java)
        }
        return pet
    }

    @Step("DELETE /pet/{petId}")
    fun delete(petId: Long) {
        Given {
            given()
        } When {
            delete("/pet/$petId")
        } Then {
            statusCode(200)
            body(equalTo("Pet deleted"))
        }
    }

}