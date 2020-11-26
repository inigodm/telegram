package inigo.client

import inigo.client.infraestructure.Client
import inigo.client.infraestructure.Repository
import inigo.config.PropertiesReader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update


class TxantxangorriBot(val brain: Brain = Brain(Repository(Client())),
                       val chatsIds: MutableList<Long> = mutableListOf(),
                       val logger: Logger = LoggerFactory.getLogger(TxantxangorriBot::javaClass.name)): TelegramLongPollingBot() {
    val TOKEN = PropertiesReader.getProperties().getProperty("token")
    val NAME = PropertiesReader.getProperties().getProperty("name")
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
        logger.info("Received : $update")
        val messageTextReceived = update!!.message.text
        val chatId = update.message.chatId
        chatsIds.add(chatId)
        logger.info("Received a message from $chatId")
        answerMessage(messageTextReceived, listOf(chatId))
        logger.info("Finished $chatId's $update")
    }

    fun answerMessage(messageTextReceived: String, chatIds: List<Long>) {
        val response = brain.answer(messageTextReceived)
        sendResponse(response, chatIds)
    }

    private fun sendResponse(response: List<String>, chatIds: List<Long>) {
        if (response.isEmpty()) {
            chatIds.forEach { sendMessage("No items found", it) }
            return
        }
        var accum = ""
        var i = 0
        response.forEach {
            if (i < 20 || !it.startsWith("<b>")) {
                i++
                accum += it
            } else {
                chatIds.forEach { chatId -> sendMessage(accum, chatId) }
                accum = it
                i = 1
            }
        }
        if (accum.isNotEmpty()) {
            chatIds.forEach { chatId -> sendMessage(accum, chatId) }
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
