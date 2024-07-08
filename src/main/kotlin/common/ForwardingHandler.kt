package org.example.common

import data.SettingData
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket


class ForwardingHandler(private val clientSocket:Socket, settingData: SettingData):Runnable {
    private val logManager = LogManager()
    private val remoteHost = settingData.remoteHost
    private val remotePort = settingData.remotePort

    override fun run() {
        try {
            Socket(remoteHost, remotePort).use { remoteSocket ->
                clientSocket.getInputStream().use { clientIn ->
                    clientSocket.getOutputStream().use { clientOut ->
                        remoteSocket.getInputStream().use { remoteIn ->
                            remoteSocket.getOutputStream().use { remoteOut ->

                                val clientToRemoteThread = Thread {
                                    try {
                                        forwardData(clientIn, remoteOut)
                                    } catch (e: IOException) {
                                        sendCuttingMessage()
                                    }
                                }

                                clientToRemoteThread.start()

                                try {
                                    forwardData(remoteIn, clientOut)
                                } catch (e: IOException) {
                                    sendCuttingMessage()
                                }

                                // クライアントからリモートサーバーへのデータ転送スレッドを待機
                                clientToRemoteThread.join()
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            sendCuttingMessage()
        } finally {
            try {
                clientSocket.close()
            } catch (e: IOException) {
                val errorMessage = "[エラー] クライアントソケットのクローズに失敗しました: ${e.message}"
                logManager.sendLog(errorMessage)
            }
        }
    }

    private fun forwardData(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(4096)
        var bytesRead: Int
        try {
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
                output.flush()
            }
        } catch (e: IOException) {
            throw e // スローして、呼び出し元で処理させる
        }
    }

    private fun sendCuttingMessage() {
        val cuttingIp = clientSocket.inetAddress
        val cuttingMessage = "[ポート転送] $cuttingIp が切断しました"
        logManager.sendLog(cuttingMessage)
    }
}