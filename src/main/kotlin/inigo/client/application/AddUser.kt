package inigo.client.application

import inigo.client.infraestructure.Users
import inigo.repository.UserRepository

class AddUsersId(val users: Users = Users(UserRepository()),
                 val chatsIds: MutableList<Long> = users.getAllUserIds().toMutableList()) {

    fun addNewUserIdIfAbsent(id: Long, name: String = "") {
        if (!chatsIds.contains(id)) {
            chatsIds.add(id)
            users.insertUser(id, name)
        }
    }

    fun findReceivers(receiversCode: Int, chatId: Long): List<Long> {
        return if (receiversCode == 1) {
            listOf(chatId)
        } else {
            chatsIds
        }
    }
}
