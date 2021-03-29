package inigo.client.application

import inigo.client.domain.ItemData
import inigo.client.infraestructure.Client
import inigo.client.infraestructure.Repository
import inigo.client.infraestructure.TxantxangorriBot
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger


class Maintest {

    val client : Client = mockk()
    val repo: Repository = mockk()
    var brain: Brain = Brain(Repository(client))
    var users : AddUsersId = mockk()
    val logger : Logger = mockk()
    val txantxangorribot = TxantxangorriBot(brain, users, logger)

    @BeforeEach
    fun setup() {
        clearMocks(client, repo, users)
    }

    /*@Test
    fun `should broadcast send new message`() {
        every { client.getAsListOf("http://localhost:8080/web/alert/ldlc", ItemData::class.java) } returns listOf(
            ItemData("test"))
        every { users.findReceivers(any(), any()) } returns listOf(42L)
        every { logger.info(any()) } returns Unit

        txantxangorribot.answerMessage("broadcast ldlc", 1)

        verify { logger.info("Mandado mensaje a 42") }
    }

    @Test
    fun `should update only a item`() {
        every { client.getAsListOf("http://localhost:8080/web/scrap/ldlc/type/tarjetagrfica", ItemData::class.java) } returns
                listOf(ItemData("test"))
        every { users.findReceivers(any(), any()) } returns listOf(42L)
        every { logger.info(any()) } returns Unit

        txantxangorribot.answerMessage("bilatuguztientzat tarjetak", 1)

        verify { logger.info("Mandado mensaje a 42") }
    }*/
}
