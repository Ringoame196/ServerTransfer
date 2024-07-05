package org.example

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.time.LocalDateTime


class ForwardingHandler(private val clientSocket:Socket, private val remoteHost:String, private val remotePort:Int):Runnable {
    override fun run() {
        try {
            Socket(remoteHost, remotePort).use { remoteSocket ->
                clientSocket.getInputStream().use { clientIn ->
                    clientSocket.getOutputStream().use { clientOut ->
                        remoteSocket.getInputStream().use { remoteIn ->
                            remoteSocket.getOutputStream().use { remoteOut ->

                                // クライアントからリモートサーバーへのデータ転送
                                Thread {
                                    try {
                                        forwardData(clientIn, remoteOut)
                                    } catch (e: IOException) {
                                        sendCuttingMessage()
                                    }
                                }.start()

                                // リモートサーバーからクライアントへのデータ転送
                                try {
                                    forwardData(remoteIn, clientOut)
                                } catch (e: IOException) {
                                    sendCuttingMessage()
                                }
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
                println("[エラー] クライアントソケットのクローズに失敗しました: ${e.message}")
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
        val time = LocalDateTime.now()
        val hour = time.hour
        val minute = time.minute
        val second = time.second
        val cuttingIp = clientSocket.inetAddress
        println("[$hour:$minute.$second] [ポート転送] $cuttingIp が切断しました")
    }
}