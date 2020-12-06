package inigo.client.application

import inigo.client.infraestructure.Users
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddUsersIdTest {
    val users : Users = mockk()
    val chatsIds : MutableList<Long> = spyk(mutableListOf(), )
    val sut = AddUsersId(users, chatsIds)

    @BeforeEach
    fun setup() {
        clearMocks(users, chatsIds)
    }

    @Test
    fun `should insert chatId if it does not exists`() {
        val id = 42L
        val name = "name"

        every { users.insertUser(id, name) } returns Unit

        sut.addNewUserIdIfAbsent(id, name)

        verify { users.insertUser(id, name) }
        verify { chatsIds.add(id) }
    }

    @Test
    fun `should not insert chatId if it does exists`() {
        val id = 42L
        val name = "name"
        chatsIds.add(id)

        sut.addNewUserIdIfAbsent(id, name)

        verify(exactly = 0) { users.insertUser(id, name) }
        verify(exactly = 1) { chatsIds.add(id) }
    }

    @Test
    fun `should return receivers of the message`() {
        chatsIds.add(41L)
        chatsIds.add(42L)

        assertEquals(sut.findReceivers(0, 42L), listOf(42L))
        assertEquals(sut.findReceivers(1, 42L), listOf(41L, 42L))
    }

}
