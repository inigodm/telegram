package inigo.client.infraestructure

import inigo.client.application.AddUsersId
import inigo.client.application.Brain
import inigo.config.PropertiesReader
import inigo.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.ParseMode
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update


class TxantxangorriBot(val brain: Brain = Brain(Repository(Client())),
                       val users: AddUsersId = AddUsersId(Users(UserRepository())),
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
        val messageTextReceived = update!!.message.text
        val chatId = update.message.chatId
        val chatName = obtainName(update)
        logger.info("Received -> ($chatId)${chatName} ${messageTextReceived}")
        if (messageTextReceived.equals("dame de baja")){
            users.deleteUSer(chatId)
        } else {
            users.addNewUserIdIfAbsent(chatId, chatName)
        }
        answerMessage(messageTextReceived, chatId)
        logger.info("Finished $chatId's $update")
    }

    private fun obtainName(update: Update): String {
        return try {
            val from = update.message.from
            "(${from.userName}) ${from.firstName} ${from.lastName}"
        } catch (e: Exception) {
            "txoritxo"
        }
    }

    fun answerMessage(messageTextReceived: String, chatId: Long = 0L) {
        sendResponse(brain.answer(messageTextReceived), users.findReceivers(brain.receivers(messageTextReceived), chatId))
    }

    private fun sendResponse(response: List<String>, to: List<Long>) {
        if (response.isEmpty()) {
            to.forEach { sendMessage("No items found", it) }
        } else {
            to.forEach { chatId -> response.forEach { sendMessage(it, chatId) } }
        }
    }

    /**
     * Return bot username of this bot
     */
    override fun getBotUsername(): String {
        return  NAME
    }

    private fun sendMessage(message: String, chatId: Long) {
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
