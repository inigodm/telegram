package inigo.client.api

import com.google.gson.Gson
import inigo.exceptions.HTTPResponseError
import io.github.rybalkinsd.kohttp.dsl.async.httpPutAsync
import io.github.rybalkinsd.kohttp.ext.asString
import io.github.rybalkinsd.kohttp.ext.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class Client(var logger: Logger = LoggerFactory.getLogger(Client::javaClass.name)) {
    fun <T : Any?> getAsListOf(url: String, clazz: Class<T>): List<T> {
        logger.info("Getting $url")
        url.httpGet().use {
            if (!it.isSuccessful) {
                throw HTTPResponseError(it.code)
            }
            logger.info("Response size ${it.body.toString().length}")
            return Gson().fromJson(it.asString(), ListParametrizedType(clazz))
        }
    }

    fun put(putUrl: String) {
        logger.info("Putting async to $putUrl")
        httpPutAsync {
            try {
                url(putUrl)
                logger.info("Put ended $putUrl")
            }catch (e: Exception){
                logger.error("Error on PUT", e)
            }
        }
        logger.info("Putted async to $putUrl")
    }
}

class ListParametrizedType<T : Any?>(val clazz: Class<T>): ParameterizedType {

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(clazz, Any::class.java)
    }

    override fun getRawType(): Type {
        return List::class.java
    }

    override fun getOwnerType(): Type? {
        return null
    }
}

fun main(args: Array<String>) {
    val client = Client()
    var logger: Logger = LoggerFactory.getLogger(Client::javaClass.name)
    logger.info("- Retrieving data")
    val res: List<ItemData> = client.getAsListOf(
        "http://localhost:8080/web/scrap/ldlc/type/impresora",
        ItemData::class.java
    )
    logger.info("- Updating data")
    client.put("http://localhost:8080/web/scrap/ldlc/type/any")
    logger.info(res.toString())
}

