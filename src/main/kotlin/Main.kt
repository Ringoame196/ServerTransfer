package org.example

import java.io.IOException
import java.net.ServerSocket


fun main() {
    println("[サーバーソケットサーバー設定]")

    try {
        print("受信ポートを入力してください:")
        val localPort = readlnOrNull()?.toInt()

        print("転送先のホストを入力してください(未入力の場合はlocalhostになります):")
        val remoteHost = readlnOrNull() ?: "localhost"

        print("転送先のポートを入力してください:")
        val remotePort = readlnOrNull()?.toInt()

        if (localPort == null || remotePort == null) {
            println("受信ポートと転送先のポートは入力してください")
            return
        }

        println()

        startServerSocket(localPort,remoteHost,remotePort) // サーバーソケットを起動
    }catch (e:NumberFormatException) {
        println("ポートは数字のみを入力してください")
    }
}

private fun startServerSocket(localPort:Int, remoteHost:String, remotePort:Int) {
    try {
        ServerSocket(localPort).use { serverSocket ->
            println("[サーバーソケット] サーバーソケット起動しました (受信ポート:${localPort})")
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