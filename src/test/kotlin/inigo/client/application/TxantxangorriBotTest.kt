package inigo.client.application

import inigo.client.infraestructure.TxantxangorriBot
import inigo.client.infraestructure.Users
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class TxantxangorriBotTest {
    private val brain : Brain = mockk()
    private val users : AddUsersId = mockk(relaxed = true)
    private val logger : Logger = mockk(relaxed = true)
    private val sut = spyk(TxantxangorriBot(brain, users, logger))

    @BeforeEach
    fun setup() {
        clearMocks(brain, users, logger)
    }
}
