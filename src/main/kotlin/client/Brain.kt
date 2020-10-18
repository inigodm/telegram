package client

import client.api.Client
import client.api.ItemData

class Brain(var client: Client) {
    val types = mapOf<String, String>(
        "tarjetas" to "tarjetagrfica",
        "tvs" to "tv",
        "memorias" to "memoriapc",
        "tablets" to "tablet",
        "camaras" to "cmaradefotos",
        "auriculares" to "auriculares",
        "ssd" to "discossd",
        "hdd" to "discodurointerno",
        "procesadores" to "procesador",
        "impresoras" to "impresoras",
        "moviles" to "mvilysmartphone",
    )
    fun answer(message: String): List<String> {
        val words = message.toLowerCase().split("\\s+".toRegex())
        if (types.keys.contains(words[0])){
            return getAnswerFor(words[0])
        }
        when (words[0]) {
            "buscar" -> return find(words)
            "update" -> return update(words[1])
        }
        return listOf("QE ISE CABESSAAA???? Yo solo se de tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, " +
                "procesadores, moviles e impresoras")
    }

    private fun find(words: List<String>): List<String> {
        if (words.size == 3){
            val res: List<ItemData> =
                client.getAsListOf("http://localhost:8080/web/scrap/ldlc/type/${types.get(words[1])}/query/${words[2]}", ItemData::class.java)
            return if (res.isEmpty()) listOf("No hay nada de eso") else res.map { formatRawData(it) }
        } else if (words.size == 2){
            val res: List<ItemData> =
                client.getAsListOf("http://localhost:8080/web/scrap/ldlc/query/${words[1]}", ItemData::class.java)
            return res.map { formatRawData(it) }
        } else {
            return listOf("Se usa asi: buscar [tipo] [palabra a buscar]")
        }
    }

    private fun update(type: String): List<String> {
        client.put("http://localhost:8080/web/scrap/ldlc/type/$type")
        return listOf("Actualizando... Tardara un ratico")
    }

    private fun getAnswerFor(type: String): List<String> {
        val res: List<ItemData> =
            client.getAsListOf("http://localhost:8080/web/scrap/ldlc/type/${types.get(type)}", ItemData::class.java)
        return res.map { formatRawData(it) }
    }

    private fun formatRawData(data: ItemData): String {
        return "<b>${data.name}-\n${data.price.toDouble()/100}€ </b>${data.desc}\n<a href='${data.url}'>link</a>\n"
    }
}
