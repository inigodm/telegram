package inigo.client.infraestructure

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Repository(var client: Client, var logger: Logger = LoggerFactory.getLogger(Repository::javaClass.name)) {
    val HOST = "localhost"

    fun getAlerts(shop: String = "ldlc") : List<ItemData>{
        logger.debug("Going to http://$HOST:8080/web/alert/$shop")
        return  client.getAsListOf("http://$HOST:8080/web/alert/$shop", ItemData::class.java)
                .sortedBy { it.type }
    }

    fun getProductsByType(type: String, shop: String = "ldlc") : List<ItemData>{
        logger.debug("Going to http://$HOST:8080/web/scrap/$shop/type/$type")
        return client.getAsListOf("http://$HOST:8080/web/scrap/$shop/type/$type", ItemData::class.java)
                .sortedBy { it.type }
    }

    fun getProductsByTypeWithQuery(type: String, query: String, shop: String = "ldlc") : List<ItemData>{
        logger.debug("Going to http://$HOST:8080/web/scrap/$shop/type/$type/query/$query")
        return client.getAsListOf("http://$HOST:8080/web/scrap/$shop/type/$type/query/$query", ItemData::class.java)
                .sortedBy { it.type }
    }

    fun getProductsByQuery(query: String, shop: String = "ldlc") : List<ItemData>{
        logger.debug("Going to http://$HOST:8080/web/scrap/$shop/query/$query")
        return client.getAsListOf("http://$HOST:8080/web/scrap/$shop/query/$query", ItemData::class.java)
                .sortedBy { it.type }
    }


    fun updateProducts(type: String, shop: String = "ldlc") {
        logger.debug("Going to http://$HOST:8080/web/scrap/$shop/type/$type")
        client.put("http://$HOST:8080/web/scrap/$shop/type/$type")
    }
}
