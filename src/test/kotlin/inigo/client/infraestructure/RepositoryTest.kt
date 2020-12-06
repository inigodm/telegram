package inigo.client.infraestructure

import inigo.client.randomItemData
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class RepositoryTest {
    private val client : Client = mockk()
    private val logger : Logger = mockk(relaxed = true)
    private val sut = Repository(client, logger)

    @BeforeEach
    fun setup() {
        clearMocks(client, logger)
    }

    @Test
    fun `should get alerts for a shop`() {
        val item = randomItemData()
        val item2 = randomItemData()
        every { client.getAsListOf("http://localhost:8080/web/alert/ldlc", ItemData::class.java) } returns listOf(item, item2)
        val res = sut.getAlerts("ldlc")

        assertEquals(res, listOf(item, item2).sortedBy { it.type })
    }

    @Test
    fun `should get product by type`() {
        val item = randomItemData()
        val item2 = randomItemData()
        every { client.getAsListOf("http://localhost:8080/web/scrap/ldlc/type/tv", ItemData::class.java) } returns listOf(item, item2)
        val res = sut.getProductsByType("tv", "ldlc")

        assertEquals(res, listOf(item, item2).sortedBy { it.type })
    }

    @Test
    fun `should get product by type and query`() {
        val item = randomItemData()
        val item2 = randomItemData()
        every { client.getAsListOf("http://localhost:8080/web/scrap/ldlc/type/tv/query/query", ItemData::class.java) } returns listOf(item, item2)
        val res = sut.getProductsByTypeWithQuery("tv", "query", "ldlc")

        assertEquals(res, listOf(item, item2).sortedBy { it.type })
    }

    @Test
    fun `should get product by query`() {
        val item = randomItemData()
        val item2 = randomItemData()
        every { client.getAsListOf("http://localhost:8080/web/scrap/ldlc/query/query", ItemData::class.java) } returns listOf(item, item2)
        val res = sut.getProductsByQuery( "query", "ldlc")

        assertEquals(res, listOf(item, item2).sortedBy { it.type })
    }

    @Test
    fun `should update products`() {
        every { client.put("http://localhost:8080/web/scrap/ldlc/type/any") } returns Unit
        sut.updateProducts( "any", "ldlc")
    }
}
