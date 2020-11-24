package inigo.client

import inigo.client.api.Client
import inigo.client.api.ItemData
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import inigo.client.randomItemData as randomItemData

class BrainTest {
    val client : Client = mockk()
    val sut = Brain(client)

    @BeforeEach
    fun setup() {
        clearMocks(client)
    }

    @Test
    fun `should select to find by query`() {
        val item = randomItemData()
        every { client.getAsListOf("http://${sut.HOST}:8080/web/scrap/ldlc/query/tvs", ItemData::class.java) } returns listOf(item)

        val response = sut.answer("buscar tvs")

        assertEquals(response, listOf(item).map { formatRawData(it) })
    }

    @Test
    fun `should select to find by type and query`() {
        val item = randomItemData()
        every { client.getAsListOf("http://${sut.HOST}:8080/web/scrap/ldlc/type/tv/query/lg", ItemData::class.java) } returns listOf(item)

        val response = sut.answer("buscar tvs LG")

        assertEquals(response, listOf(item).map { formatRawData(it) })
    }

    @Test
    fun `should return not found when only a parameter`() {
        val response = sut.answer("buscar")

        assertEquals(response, listOf("Se usa asi: buscar [tipo] [palabra a buscar]"))
    }

    @Test
    fun `should return a message when word not recognized`() {
        val response = sut.answer("fdsfsd")

        assertEquals(response, listOf("""QE ISE CABESSAAA???? Yo solo se de tipos: tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, 
                "procesadores, moviles e impresoras
                Ademas estan los comandos:
                buscar <tipo?> <palabra a buscar>
                nuevas 
                """))
    }

    @Test
    fun `should ask for new products`() {
        val item1 = randomItemDataOfType("type1")
        val item2 = randomItemDataOfType("type2")
        val item3 = randomItemDataOfType("type1")
        val item4 = randomItemDataOfType("type1")
        every { client.getAsListOf("http://${sut.HOST}:8080/web/alert/lcdl", ItemData::class.java) } returns listOf(item1, item2, item3, item4)

        val response = sut.answer("nuevas lcdl")

        assertEquals(response.joinToString(", "), "<b>type1</b>\n${formatRawData(item1)}, ${formatRawData(item3)}, ${formatRawData(item4)}, <b>type2</b>\n${formatRawData(item2)}")

    }

    private fun formatRawData(data: ItemData): String {
        return "${data.name}-\n${data.price.toDouble()/100}€ ${data.desc}\n<a href='${data.url}'>link</a>\n"
    }
}
