package inigo.client

import inigo.client.infraestructure.TxantxangorriBot
import inigo.client.infraestructure.Client
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.logging.Logger

fun main(args: Array<String>) {
    Logger.getLogger("main").info("Starting-------------------")
    if (args.size > 0){
        if (args[0] == "update"){
            executeUpdate()
        } else {
            sendNewMessage()
        }
    }else{
        runService()
    }
    Logger.getLogger("main").info("End-------------------")
}

fun sendNewMessage() {
    TxantxangorriBot().answerMessage("broadcast ldlc", 1)
}

fun executeUpdate() {
    val client = Client()
    Logger.getLogger("main").info("- Updating data")
    client.put("http://localhost:8080/web/scrap/ldlc/type/any")
    Logger.getLogger("main").info("-----Async process started. Nothing more to do here-----")
}

fun runService() {
    ApiContextInitializer.init()
    try {
        TelegramBotsApi().registerBot(TxantxangorriBot())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}
