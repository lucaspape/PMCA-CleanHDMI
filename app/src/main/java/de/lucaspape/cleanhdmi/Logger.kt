package de.lucaspape.cleanhdmi

import android.os.Environment
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

object Logger {
    val file: File
        get() = File(
            Environment.getExternalStorageDirectory(),
            "PMCADEMO/LOG.TXT"
        )

    internal fun log(msg: String?) {
        try {
            file.parentFile.mkdirs()
            val writer =
                BufferedWriter(FileWriter(file, true))
            writer.append(msg)
            writer.newLine()
            writer.close()
        } catch (e: IOException) {
        }
    }

    internal fun log(type: String, msg: String) {
        log("[$type] $msg")
    }

    fun info(msg: String) {
        log("INFO", msg)
    }

    fun error(msg: String) {
        log("ERROR", msg)
    }
}
