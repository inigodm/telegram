package client.log

import java.io.File
import java.io.ObjectInput
import java.io.PrintStream
import java.time.LocalDateTime

class Logger(var filenameRoot: String = "", var filePath: String = "/home/inigo") {
    lateinit var file : PrintStream
    var currentFilename = ""
    var local = LocalDateTime.now()

    companion object Builder {
        val printer: Logger = Logger()

        fun getLogger(): Logger {
            return printer
        }
    }

    fun printLine(message: Any) {
        setupFile()
        printLog("${local.hour}:${local.minute}${local.second}->${message.toString()}")
    }

    fun printError(e: Exception, msg: String = ""){
        setupFile()
        if (!msg.isEmpty()) { file.println(msg) }
        e.printStackTrace(file)
    }

    private fun printLog(message: String) {
        file.println(message)
    }

    private fun setupFile() {
        val filename = buildFilename()
        if (currentFilename != filename) {
            file.flush()
            file.close()
            file = PrintStream(File(filename))
            currentFilename = filename
        }
    }

    private fun buildFilename() : String {
        local = LocalDateTime.now()
        return "${filenameRoot}${local.year}${local.month}${local.dayOfMonth}"
    }
}
