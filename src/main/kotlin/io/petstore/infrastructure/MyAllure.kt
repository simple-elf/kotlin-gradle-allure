package io.petstore.infrastructure

import io.qameta.allure.Allure
import io.qameta.allure.Allure.attachment
import io.qameta.allure.Allure.step
import io.qameta.allure.AllureLifecycle
import io.qameta.allure.attachment.DefaultAttachmentProcessor
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer
import io.qameta.allure.attachment.http.HttpRequestAttachment
import io.qameta.allure.attachment.http.HttpResponseAttachment
import io.restassured.filter.Filter
import io.restassured.filter.FilterContext
import io.restassured.filter.OrderedFilter
import io.restassured.internal.NameAndValue
import io.restassured.internal.support.Prettifier
import io.restassured.response.Response
import io.restassured.specification.FilterableRequestSpecification
import io.restassured.specification.FilterableResponseSpecification
import java.util.*
import kotlin.collections.HashMap

abstract class MyAllure : OrderedFilter {
    
    fun setRequestTemplate(templatePath: String): MyAllure {
        requestTemplatePath = templatePath
        return this
    }

    fun setResponseTemplate(templatePath: String): MyAllure {
        responseTemplatePath = templatePath
        return this
    }
    
    companion object : Filter {
        private var requestTemplatePath = "http-request.ftl"
        private var responseTemplatePath = "http-response.ftl"
        
        private fun toMapConverter(items: Iterable<NameAndValue?>): Map<String, String> {
            val result: MutableMap<String, String> =
                HashMap()
            items.forEach { h: NameAndValue? -> result[h!!.name] = h.value }
            return result
        }

        override fun filter(
            requestSpec: FilterableRequestSpecification,
            responseSpec: FilterableResponseSpecification,
            filterContext: FilterContext
        ): Response {
            val prettifier = Prettifier()
            val requestAttachmentBuilder =
                HttpRequestAttachment.Builder.create("Request", requestSpec.uri)
                    .setMethod(requestSpec.method)
                    .setHeaders(toMapConverter(requestSpec.headers))
                    .setCookies(toMapConverter(requestSpec.cookies))
            if (Objects.nonNull(requestSpec.getBody())) {
                requestAttachmentBuilder.setBody(prettifier.getPrettifiedBodyIfPossible(requestSpec))
            }
            val requestAttachment = requestAttachmentBuilder.build()

            step(requestSpec.method + requestSpec.derivedPath, Allure.ThrowableRunnableVoid {
                attachment("Request", requestAttachment.toString())
            })
            DefaultAttachmentProcessor().addAttachment(
                requestAttachment,
                FreemarkerAttachmentRenderer(requestTemplatePath)
            )


            val response = filterContext.next(requestSpec, responseSpec)
            val responseAttachment =
                HttpResponseAttachment.Builder.create(response.statusLine)
                    .setResponseCode(response.statusCode)
                    .setHeaders(toMapConverter(response.headers))
                    .setBody(prettifier.getPrettifiedBodyIfPossible(response, response.body))
                    .build()
            DefaultAttachmentProcessor().addAttachment(
                responseAttachment,
                FreemarkerAttachmentRenderer(responseTemplatePath)
            )
            return response
        }
    }

    override fun getOrder(): Int {
        return Int.MAX_VALUE
    }
}
