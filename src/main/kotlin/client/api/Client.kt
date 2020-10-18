package client.api

import com.google.gson.Gson
import exceptions.HTTPResponseError
import io.github.rybalkinsd.kohttp.dsl.async.httpPutAsync
import io.github.rybalkinsd.kohttp.ext.asString
import io.github.rybalkinsd.kohttp.ext.httpGet
import io.github.rybalkinsd.kohttp.ext.url
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class Client {
    fun <T : Any?> getAsListOf(url: String, clazz: Class<T>): List<T> {
        url.httpGet().use {
            if (!it.isSuccessful) {
                throw HTTPResponseError(it.code())
            }
            val gson = Gson()
            return gson.fromJson(it.asString(), ListParametrizedType(clazz))
        }
    }

    fun put(putUrl: String) {
        httpPutAsync {
            url(putUrl)
        }
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
    println("- Retrieving data")
    val res: List<ItemData> = client.getAsListOf("http://localhost:8080/web/scrap/ldlc/type/impresora", ItemData::class.java)
    println("- Updating data")
    client.put("http://localhost:8080/web/scrap/ldlc/type/any")
    println(res)
}
