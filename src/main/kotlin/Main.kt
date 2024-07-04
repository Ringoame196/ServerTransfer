package org.example

import java.io.IOException
import java.net.ServerSocket


fun main() {
    val localPort = 19132 // 受信ポート
    val remoteHost = "localhost" // 転送先のホスト
    val remotePort = 25565 // 転送先のポート

    startServerSocket(localPort,remoteHost,remotePort)
}

fun startServerSocket(localPort:Int, remoteHost:String, remotePort:Int) {
    try {
        ServerSocket(localPort).use { serverSocket ->
            println("[サーバーソケット] サーバーソケット起動しました (Port:${localPort})")
            while (true) {
                val clientSocket = serverSocket.accept()
                println("[ポート転送] ${clientSocket.inetAddress} -> ${remoteHost}:$remotePort")

                // クライアントソケットとリモートソケットを処理する新しいスレッドを開始
                Thread(ForwardingHandler(clientSocket, remoteHost, remotePort)).start()
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}