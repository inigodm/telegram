package client

import client.api.Client
import client.api.ItemData
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
    val chatIds =  mutableListOf(728173703L)
    val txantxangorri = TxantxangorriBot(chatsIds = chatIds)
    txantxangorri.answerMessage("nuevas ldlc", chatIds[0])
}

fun executeUpdate() {
    val client = Client()
    Logger.getLogger("main").info("- Updating data")
    client.put("http://localhost:8080/web/scrap/ldlc/type/any")
    Logger.getLogger("main").info("-----Async process started. Nothing more to do here-----")
}

fun runService() {
    ApiContextInitializer.init()
    val telegramBotsApi = TelegramBotsApi()
    try {
        telegramBotsApi.registerBot(TxantxangorriBot())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}
