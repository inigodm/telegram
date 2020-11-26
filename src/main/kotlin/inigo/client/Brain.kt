package inigo.client

import inigo.client.infraestructure.ItemData
import inigo.client.infraestructure.Repository

class Brain(var repo: Repository) {
    val HOST = "localhost"
    val types = mapOf(
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
            "nuevas" -> return news(words)
            "bilatu" -> return find(words)
            "berriak" -> return find(words)
        }
        return listOf("""Ze oxxxtia diozu burundi!!! 
                
            Erabil dezakezuen komanduek dabe (bai, itzulitak xD):
                --> bilatu [mota?] [hitz bat bilatzeko (a.g: GTX, RTX, amd...)]
                --> berriak
                 
            Ta [mota]-k honako hauek izan daitezke:
            tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, procesadores, moviles e impresoras
            
            --
            
            QE ISE CABESSAAA???? 
                
            Estos son los comandos que puedes utilizar (Si he traducido los comandos XD):
                --> buscar [tipo?] [palabra a buscar (p.e GTX, RTX, amd...)]
                --> nuevas
                 
            Y los [tipo]s son estos:
            
            tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, 
                procesadores, moviles e impresoras
                """)
    }

    private fun find(words: List<String>): List<String> {
        if (words.size == 3){
            val res: List<ItemData> = repo.getProductsByTypeWithQuery(types.get(words[1]) ?: "any", words[2])
            return if (res.isEmpty()) listOf("No hay nada de eso") else res.map { formatRawData(it) }
        } else if (words.size == 2){
            return repo.getProductsByQuery(words[1]).map { formatRawData(it) }
        } else {
            return listOf("""Se usa asi: buscar [tipo] [palabra a buscar]
                Erablitzeko era: buscar [mota] [bilatzeko hitza]
            """.trimMargin())
        }
    }

    private fun update(type: String): List<String> {
        repo.updateProducts(type)
        return listOf("Scraping eta informazioa gordetzen... minutu batzuk barru izango dira erantzun berriak")
    }

    private fun getAnswerFor(type: String): List<String> {
        val res: List<ItemData> = repo.getProductsByType(types.get(type) ?: "")
        return res.map { formatRawData(it) }
    }

    private fun news(words: List<String>): List<String> {
        val res: List<ItemData> = repo.getAlerts(words[1]);
        var type = ""
        return res.map {
            var str = ""
            if (type != it.type){
                type = it.type
                str += "<b><u>\n\n${obtainTittle(type)}</u></b>\n\n"
            }
            str + formatRawData(it)
        }
    }

    private fun obtainTittle(type: String): String {
        val title = types.filter { (k, v) -> v.equals(type) }.keys.joinToString("")
        return if (title.isBlank()) type else title
    }

    private fun formatRawData(data: ItemData): String {
        return "${data.name}-\n${data.price.toDouble()/100}€ ${data.desc}\n<a href='${data.url}'>link</a>\n"
    }
}
