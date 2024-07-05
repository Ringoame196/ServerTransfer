package org.example

import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

class Server(private val localPort: Int, private val remoteHost: String, private val remotePort: Int) {
    private val isRunning = AtomicBoolean(true)
    private lateinit var serverSocket: ServerSocket

    fun start() {
        try {
            serverSocket = ServerSocket(localPort)
            sendServerInfo()
            println("[サーバーソケット] サーバーソケット起動しました (受信ポート:$localPort -> 転送先: $remoteHost:$remotePort)\n")

            while (isRunning.get()) {
                try {
                    val clientSocket = serverSocket.accept()
                    println("[ポート転送] ${clientSocket.inetAddress} -> $remoteHost:$remotePort")
                    Thread(ForwardingHandler(clientSocket, remoteHost, remotePort)).start()
                } catch (e: IOException) {
                    if (isRunning.get()) {
                        println("[エラー] クライアントの接続処理中にエラーが発生しました: ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            println("[エラー] サーバー起動に失敗しました: ${e.message}")
        } finally {
            try {
                serverSocket.close()
            } catch (e: IOException) {
                println("[エラー] サーバーソケットのクローズに失敗しました: ${e.message}")
            }
        }
    }

    fun stop() {
        println("[サーバー] サーバーを停止しています...")
        isRunning.set(false)
        try {
            serverSocket.close()
        } catch (e: IOException) {
            println("[エラー] サーバーソケットのクローズに失敗しました: ${e.message}")
        }
    }

    private fun sendServerInfo() {
        val localHost = InetAddress.getLocalHost()
        val privateIP = localHost.hostAddress
        val globalIP = try {
            URL("https://api.ipify.org").readText()
        } catch (e: Exception) {
            "取得失敗"
        }

        println("[サーバーソケット情報]")
        println("プライベートIP: $privateIP")
        println("グローバルIP: $globalIP")
    }
}