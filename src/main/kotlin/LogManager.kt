package org.example

import java.io.File
import java.time.LocalDateTime

class LogManager {
    fun sendLog(log: String) {
        makeLogFolder()
        val time = LocalDateTime.now()
        val hour = time.hour
        val minute = time.minute
        val second = time.second
        val sendMessage = "[$hour:$minute.$second]$log"
        writeLogFile(sendMessage)
        println(sendMessage)
    }
    private fun writeLogFile(log:String) {
        val time = LocalDateTime.now()
        val year = time.year
        val month = time.monthValue
        val day = time.dayOfMonth
        val filePath = "./log/$year-$month-$day.log"
        val logFile = File(filePath)

        logFile.appendText(log + "\n")
    }
    private fun makeLogFolder() {
        val logFolderPath = "./log/"
        if (!File(logFolderPath).exists()) {
            File(logFolderPath).mkdirs()
        }
    }
}