package inigo.client.application

import inigo.client.infraestructure.ItemData
import inigo.client.infraestructure.Repository

class Brain(var repo: Repository) {
    val RESPONSE_TO_SENDER = 0
    val RESPONSE_TO_ALL = 1
    private val types = mapOf(
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

    private val verbResponses = mapOf(
        "buscar" to Pair(Brain::find, RESPONSE_TO_SENDER),
        "bilatu" to Pair(Brain::find, RESPONSE_TO_SENDER),
        "update" to Pair(Brain::update, RESPONSE_TO_SENDER),
        "nuevas" to Pair(Brain::news, RESPONSE_TO_SENDER),
        "berriak" to Pair(Brain::news, RESPONSE_TO_SENDER),
        "broadcast" to Pair(Brain::news, RESPONSE_TO_ALL)
    )

    fun answer(message: String): List<String> {
        val words = message.toLowerCase().split("\\s+".toRegex())

        if (verbResponses.keys.contains(words[0])) {
            return verbResponses[words[0]]!!.first.invoke(this, words)
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
            return if (res.isEmpty()) listOf("No hay nada de eso") else res.map { obtainItemData(it) }
        } else if (words.size == 2){
            return repo.getProductsByQuery(words[1]).map { obtainItemData(it) }
        } else {
            return listOf("""Se usa asi: buscar [tipo] [palabra a buscar]
                Erablitzeko era: buscar [mota] [bilatzeko hitza]
            """.trimMargin())
        }
    }

    private fun update(words: List<String>): List<String> {
        repo.updateProducts(words[1])
        return listOf("Scraping eta informazioa gordetzen... minutu batzuk barru izango dira erantzun berriak")
    }

    private fun format(items: List<ItemData>): List<String> {
        var i = 0
        var type = ""
        var accum = ""
        val res = mutableListOf<String>()
        items.forEach {
            var str = ""
            if (type != it.type) {
                type = it.type
                str += "<b><u>\n\n${obtainTittle(type)}</u></b>\n\n"
            }
            str += obtainItemData(it)
            if (i < 20 || !str.startsWith("<b>")) {
                i++
                accum += str
            } else {
                res.add(accum)
                accum = str
                i = 1
            }
        }
        res.add(accum)
        return res
    }

    private fun news(words: List<String>): List<String> {
        val res: List<ItemData> = repo.getAlerts(words[1]);
        return format(res)
    }

    private fun obtainTittle(type: String): String {
        val title = types.filter { (k, v) -> v == type }.keys.joinToString("")
        return if (title.isBlank()) type else title
    }

    private fun obtainItemData(data: ItemData): String {
        return "${data.name}-\n${data.price.toDouble()/100}€ ${data.desc}\n<a href='${data.url}'>link</a>\n"
    }

    fun receivers(message: String): Int {
        val words = message.toLowerCase().split("\\s+".toRegex())
        if (verbResponses.keys.contains(words[0])) {
            return verbResponses[words[0]]!!.second
        }
        return RESPONSE_TO_SENDER
    }
}
