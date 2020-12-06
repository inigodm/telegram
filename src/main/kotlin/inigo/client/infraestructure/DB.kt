package inigo.repository

import inigo.config.PropertiesReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

val PATH = PropertiesReader.getProperties().getProperty("db_path")

class UserRepository(dataBaseFile: String = "telegram.db", var logger: Logger = LoggerFactory.getLogger(UserRepository::class.java)) {
    val DB_PATH = "jdbc:sqlite:$PATH/sqlite/"
    var conn: Connection? = null
    val URL : String = "$DB_PATH$dataBaseFile"

    init{
        Class.forName("org.sqlite.JDBC")
    }

    fun executeCommand(command: String, vars: List<Any>) {
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

    fun findBy(sql: String, vars: List<Any> = listOf()): MutableList<Any> {
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
}

fun <T> Any.isInstanceOf(expectedClass: Class<T>) = expectedClass.isInstance(this)
