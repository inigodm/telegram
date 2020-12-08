package inigo.client.application

import inigo.client.infraestructure.Client
import inigo.client.domain.ItemData
import inigo.client.infraestructure.Repository
import inigo.client.randomItemDataOfType
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import inigo.client.randomItemData as randomItemData

class BrainTest {
    val client : Client = mockk()
    val repo: Repository = mockk()
    val sut = Brain(repo)

    @BeforeEach
    fun setup() {
        clearMocks(client, repo)
    }

    @Test
    fun `should select to find by query`() {
        val item = randomItemData()
        every { repo.getProductsByQuery("tvs") } returns listOf(item)
        var response = sut.answer("buscar tvs")

        assertEquals(response, sut.format(listOf(item)))

        response = sut.answer("bilatu tvs")

        assertEquals(response, sut.format(listOf(item)))
    }

    @Test
    fun `should select to find by type and query`() {
        val item = randomItemData()
        every { repo.getProductsByTypeWithQuery("tv", "lg", "ldlc") } returns listOf(item)

        val response = sut.answer("buscar tvs LG")

        assertEquals(response, sut.format(listOf(item)))
    }

    @Test
    fun `should return not found when only a parameter`() {
        val response = sut.answer("buscar")

        assertEquals(response, listOf("""Se usa asi: buscar [tipo] [palabra a buscar]
                Erablitzeko era: buscar [mota] [bilatzeko hitza]
            """.trimMargin()))
    }

    @Test
    fun `should update when asked for`() {
        every{ repo.updateProducts("ldlc") } returns Unit
        val response = sut.answer("update ldlc")

        assertEquals(response, listOf("Scraping eta informazioa gordetzen... minutu batzuk barru izango dira erantzun berriak"))
        verify { repo.updateProducts("ldlc") }
    }

    @Test
    fun `should return a message when word not recognized`() {
        val response = sut.answer("fdsfsd")

        assertEquals(response, listOf("""Ze oxxxtia diozu burundi!!! 
                
            Erabil dezakezuen komanduek dabe (bai, itzulitak xD):
                --> bilatu [mota?] [hitz bat bilatzeko (a.g: GTX, RTX, amd...)]
                --> berriak
                 
            Ta [mota]-k honako hauek izan daitezke:
            tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, procesadores, moviles e impresoras
            
            --
            
            QE ISE CABESSAAA???? 
                
            Estos son los comandos que puedes utilizar (Si he traducido los comandos XD):
                --> buscar [tipo?] [palabra a buscar (p.e GTX, RTX, amd...)]
                --> nuevas
                 
            Y los [tipo]s son estos:
            
            tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, 
                procesadores, moviles e impresoras
                """))
    }

    @Test
    fun `should ask for new products`() {
        val item1 = randomItemDataOfType("type1")
        val item2 = randomItemDataOfType("type2")
        val item3 = randomItemDataOfType("type1")
        val item4 = randomItemDataOfType("type1")
        every { repo.getAlerts(any()) } returns listOf(item1, item3, item4, item2)

        val response = sut.answer("nuevas lcdl")

        assertEquals(response.joinToString(""), """<b><u>

type1</u></b>

${formatRawData(item1)}${formatRawData(item3)}${formatRawData(item4)}<b><u>

type2</u></b>

${formatRawData(item2)}
""".trimMargin())
        val response2 = sut.answer("berriak lcdl")

        assertEquals(response, response2)
    }

    @Test
    fun `should broadcast for new products`() {
        val item1 = randomItemDataOfType("type1")
        val item2 = randomItemDataOfType("type2")
        val item3 = randomItemDataOfType("type1")
        val item4 = randomItemDataOfType("type1")
        every { repo.getAlerts(any()) } returns listOf(item1, item3, item4, item2)

        val response = sut.answer("broadcast lcdl")

        assertEquals(response.joinToString(""), """<b><u>

type1</u></b>

${formatRawData(item1)}${formatRawData(item3)}${formatRawData(item4)}<b><u>

type2</u></b>

${formatRawData(item2)}
""".trimMargin())
    }

    @Test
    fun `Should get diferent receivers for different answers` () {
        assertEquals(sut.receivers("nuevas"), sut.RESPONSE_TO_SENDER)
        assertEquals(sut.receivers("berriak"), sut.RESPONSE_TO_SENDER)
        assertEquals(sut.receivers("buscar"), sut.RESPONSE_TO_SENDER)
        assertEquals(sut.receivers("bilatu"), sut.RESPONSE_TO_SENDER)
        assertEquals(sut.receivers("update"), sut.RESPONSE_TO_SENDER)
        assertEquals(sut.receivers("broadcast"), sut.RESPONSE_TO_ALL)
        assertEquals(sut.receivers("tete"), sut.RESPONSE_TO_SENDER)
    }

    private fun formatRawData(data: ItemData): String {
        return "${data.name}-\n${data.price.toDouble()/100}€ ${data.desc}\n<a href='${data.url}'>link</a>\n"
    }
}
