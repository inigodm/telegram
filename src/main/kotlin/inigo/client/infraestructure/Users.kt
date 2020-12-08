package inigo.client.infraestructure

import inigo.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Users(var repo: UserRepository, var logger: Logger = LoggerFactory.getLogger(UserRepository::class.java)) {
    fun insertUser(id: Long, name: String) {
        try{
            repo.executeCommand("insert into users (id, name) values (?, ?)", listOf(id, name))
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }

    fun getAllUserIds(): List<Long> {
        return repo.findBy("Select * from users").map { it as List<Any> }.map { "${it[0]}".toLong() }
    }

    fun delete(id: Long) {
        try{
            repo.executeCommand("delete from users where id = ?", listOf(id))
        } catch (e: Exception) {
            logger.error(e.message, e)
        }
    }
}
