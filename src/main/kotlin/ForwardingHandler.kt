package org.example

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket


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
        }
    }

    private fun forwardData(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (`in`.read(buffer).also { bytesRead = it } != -1) {
            out.write(buffer, 0, bytesRead)
            out.flush()
        }
    }
    private fun sendCuttingMessage() {
        val cuttingIp = clientSocket.inetAddress
        println("[サーバー転送] $cuttingIp が切断しました")
    }
}