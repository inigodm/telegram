package inigo.client.infraestructure

import inigo.repository.UserRepository
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.slf4j.Logger

class UsersTest {
    private val client: Client = mockk()
    private val logger: Logger = mockk(relaxed = true)
    private val repo: UserRepository = mockk()
    private val sut = Users(repo, logger)

    @BeforeEach
    fun setup() {
        clearMocks(client, repo, logger)
    }

    @Test
    fun `should insert users`() {
        val id = 555L
        val name = "name"
        every { repo.executeCommand("insert into users (id, name) values (?, ?)", listOf(id, name)) } returns Unit

        sut.insertUser(id, name)
    }

    @Test
    fun `should get all users ids`() {
        every { repo.findBy("Select * from users") } returns mutableListOf(listOf(1L, ""), listOf(2L, ""), listOf(3L, ""))

        val res = sut.getAllUserIds()

        assertEquals(res, mutableListOf(1L, 2L, 3L))
    }
}
