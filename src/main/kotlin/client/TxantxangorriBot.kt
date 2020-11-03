package client

import client.api.Client
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update


class TxantxangorriBot(val brain: Brain = Brain(Client()),
                       val chatsIds: MutableList<Long> = mutableListOf(),
                       val logger: Logger = LoggerFactory.getLogger(TxantxangorriBot::javaClass.name)): TelegramLongPollingBot() {
    val TOKEN = "1309064279:AAFLFm6TrWeWUk_A510WSDj9pOiRiAghD28"
    val NAME = "Txantxangorri_bot"
    /**
     * Returns the token of the bot to be able to perform Telegram Api Requests
     * @return Token of the bot
     */
    override fun getBotToken(): String {
        return TOKEN
    }

    /**
     * This method is called when receiving updates via GetUpdates method
     * @param update Update received
     */
    override fun onUpdateReceived(update: Update?) {
        val messageTextReceived = update!!.message.text
        val chatId = update.message.chatId
        chatsIds.add(chatId)
        logger.info("Received a message from $chatId")
        answerMessage(messageTextReceived, chatId)
    }

    fun answerMessage(messageTextReceived: String, chatId: Long) {
        var accum = ""
        var i = 0
        brain.answer(messageTextReceived).forEach {
            if (i < 20) {
                i++
                accum += it
            } else {
                sendMessage(accum, chatId)
                accum = ""
                i = 0
            }
        }
        if (accum.isNotEmpty()) {
            sendMessage(accum, chatId)
        }
    }

    /**
     * Return bot username of this bot
     */
    override fun getBotUsername(): String {
        return  NAME
    }

    fun sendMessage(message: String, chatId: Long) {
        val sendMessage = SendMessage()
                .setChatId(chatId)
                .setParseMode(ParseMode.HTML)
                .setText(message)
        logger.info("Mandando mensaje: $message a $chatId")
        try{
            execute(sendMessage)
        } catch (e: Exception){
            e.printStackTrace()
        }
        logger.info("Mandado mensaje: $message a $chatId")
    }
}
