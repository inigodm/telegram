package inigo.client

import com.github.javafaker.Faker
import inigo.client.domain.ItemData

fun randomItemData() : ItemData {
    val faker = Faker()
    return ItemData(
            faker.gameOfThrones().character(),
            faker.princessBride().quote(),
            faker.number().numberBetween(100, 10000),
            mutableMapOf("one" to "two"),
            faker.ancient().hero(),
            faker.ancient().god(),
            faker.internet().url())
}


fun randomItemDataOfType(type: String) : ItemData {
    val faker = Faker()
    return ItemData(
            faker.gameOfThrones().character(),
            faker.princessBride().quote(),
            faker.number().numberBetween(100, 10000),
            mutableMapOf("one" to "two"),
            faker.ancient().hero(),
            type,
            faker.internet().url())
}
