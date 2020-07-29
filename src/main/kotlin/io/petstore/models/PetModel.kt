package io.petstore.models

import org.apache.commons.lang3.RandomStringUtils

data class PetModel(
    val id: Long = RandomStringUtils.randomNumeric(4).toLong(),
    val name: String = "",
    val category: Category? = Category(),
    val status: String = Status.Available.value, // available, pending, sold
    val photoUrls: List<String> = listOf(),
    val tags: List<Tag> = listOf()
) {
    data class Category(
        val id: Int = RandomStringUtils.randomNumeric(1).toInt(),
        val name: String = ""
    )

    data class Tag(
        val id: Int = RandomStringUtils.randomNumeric(1).toInt(),
        val name: String = ""
    )

    enum class Status(val value: String) {
        Available("available"),
        Pending("pending"),
        Sold("sold")
    }

}

