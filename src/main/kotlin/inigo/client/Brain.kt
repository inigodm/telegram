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
        }
        return listOf("""QE ISE CABESSAAA???? Yo solo se de tipos: tarjetas, tvs, memorias, tablets, camaras, auriculares, ssd, hdd, 
                "procesadores, moviles e impresoras
                Ademas estan los comandos:
                buscar <tipo?> <palabra a buscar>
                nuevas 
                """)
    }

    private fun find(words: List<String>): List<String> {
        if (words.size == 3){
            val res: List<ItemData> = repo.getProductsByTypeWithQuery(types.get(words[1]) ?: "any", words[2])
            return if (res.isEmpty()) listOf("No hay nada de eso") else res.map { formatRawData(it) }
        } else if (words.size == 2){
            return repo.getProductsByQuery(words[1]).map { formatRawData(it) }
        } else {
            return listOf("Se usa asi: buscar [tipo] [palabra a buscar]")
        }
    }

    private fun update(type: String): List<String> {
        repo.updateProducts(type)
        return listOf("Actualizando... Tardara un ratico")
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
                str += "<b><u>${types.filter { (k, v) -> k.equals(type) }}</u></b>\n"
            }
            str + formatRawData(it)
        }
    }

    private fun formatRawData(data: ItemData): String {
        return "${data.name}-\n${data.price.toDouble()/100}€ ${data.desc}\n<a href='${data.url}'>link</a>\n"
    }
}
