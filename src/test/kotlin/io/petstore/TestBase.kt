package io.petstore

import io.qameta.allure.Allure
import io.qameta.allure.Step
import io.qameta.allure.restassured.MyAllureRestAssured
import io.restassured.RestAssured.*
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.filter.log.ErrorLoggingFilter
import io.restassured.filter.log.RequestLoggingFilter
import io.restassured.filter.log.ResponseLoggingFilter
import io.restassured.http.ContentType
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import java.util.*

//@DisplayName("Проверка API")
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class TestBase {

    @BeforeAll
    @DisplayName("BeforeAll_")
    fun beforeAll() {
        //println("BeforeAll")
        basePath = "/api/v3"
        baseURI = "http://localhost"
        port = 8080
        requestSpecification = RequestSpecBuilder()
            //.addFilter(AllureRestAssured().setRequestTemplate("http-request.ftl").setResponseTemplate("http-response.ftl"))
            .addFilter(MyAllureRestAssured())
            //.addFilter(MyAllure)
            .addFilter(RequestLoggingFilter())
            .addFilter(ResponseLoggingFilter())
            .addFilter(ErrorLoggingFilter())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .build()
        responseSpecification = ResponseSpecBuilder()
            .expectStatusCode(200)
            .expectContentType(ContentType.JSON)
            .build()
    }

    @BeforeEach
    fun beforeEach() {

    }



}