package inigo.client.infraestructure

import inigo.repository.UserRepository
import java.sql.DriverManager
import java.sql.SQLException

var TABLE_PRODUCTS_CREATE = """CREATE TABLE IF NOT EXISTS users
                        (id integer PRIMARY KEY,
                        name TEXT NOT NULL)"""
var TEST_INSERT = """insert into users (id, name) values (728173703L, 'inigo');
    insert into users (id, name) values (532308270L, 'tomas');""".trimMargin()


fun main(args: Array<String>) {
    val repo = UserRepository()
    val users = Users(repo)
    repo.createDatabase()
    users.insertUser(728173703L, "inigo")
    users.insertUser(532308270L, "tomas")
}

fun UserRepository.createDatabase() {
    try {
        val conn = DriverManager.getConnection(URL)
        if (conn != null) {
            val meta = conn!!.metaData
            logger.warn("The driver name is " + meta.driverName)
            logger.warn("A new database has been created.")
            conn.createStatement().use {
                println("OK -> ${it.execute(TABLE_PRODUCTS_CREATE)}")
            }
            conn.close()
        }
    } catch (e: SQLException) {
        logger.error(e.message, e)
        conn?.close()
    }
}
