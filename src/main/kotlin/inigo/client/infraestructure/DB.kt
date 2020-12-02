package inigo.repository

import inigo.config.PropertiesReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

val PATH = PropertiesReader.getProperties().getProperty("db_path")

var TABLE_PRODUCTS_CREATE = """CREATE TABLE IF NOT EXISTS users
                        (id integer PRIMARY KEY,
                        name TEXT NOT NULL)"""
var TEST_INSERT = """insert into users (id, name) values (728173703L, 'inigo');
    insert into users (id, name) values (532308270L, 'tomas');""".trimMargin()

class UserRepository(dataBaseFile: String = "telegram.db", var logger: Logger = LoggerFactory.getLogger(UserRepository::class.java)) {
    val DB_PATH = "jdbc:sqlite:$PATH/sqlite/"
    var conn: Connection? = null
    val URL : String = "$DB_PATH$dataBaseFile"

    init{
        Class.forName("org.sqlite.JDBC")
    }

    fun createDatabase() {
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

    fun insertUser(id: Long, name: String) {
        executeCommand("insert into users (id, name) values (?, ?)", listOf(id, name))
    }

    fun getAllUserIds(): List<Long> {
        return findBy("Select * from users").map { it as List<Any> }.map { it[0] as Long }
    }

    private fun executeCommand(command: String, vars: List<Any>) {
        DriverManager.getConnection(URL).use { conn ->
            var i = 1
            conn!!.prepareStatement(command).use { statement ->
                vars.forEach {
                    if (it.isInstanceOf(String::class.java)) {
                        statement.setString(i, it as String)
                    } else {
                        statement.setInt(i, (it as Long).toInt())
                    }
                    i++
                }
                logger.trace("OK -> ${statement.executeUpdate()}")
            }
        }
    }

    private fun findBy(sql: String, vars: List<Any> = listOf()): MutableList<Any> {
        DriverManager.getConnection(URL).use { conn ->
            val statement = conn?.prepareStatement(sql)!!
            val list = mutableListOf<Any>()
            var i = 1
            statement.use {
                vars.forEach{
                    if (it.isInstanceOf(String::class.java)) {
                        statement.setString(i, it as String)
                    }
                    else {
                        statement.setInt(i, it as Int)
                    }
                    i++
                }
                val rs = statement.executeQuery()
                while (rs.next()) {
                    list.add(listOf(rs.getString(1), rs.getInt(2)));
                }
            }
            return list
        }
    }

    private fun isConnected() = conn != null && !conn!!.isClosed()

}

fun <T> Any.isInstanceOf(expectedClass: Class<T>) = expectedClass.isInstance(this)


fun main(args: Array<String>) {
    val repo = UserRepository()
    repo.createDatabase()
    repo.insertUser(728173703L, "inigo")
    repo.insertUser(532308270L, "tomas")
}
