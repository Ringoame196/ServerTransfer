package org.example

import java.net.InetAddress
import java.net.ServerSocket
import java.net.URL


fun main() {
    println("[サーバーソケットサーバー設定]")
    try {
        print("受信ポートを入力してください:")
        val localPort = readlnOrNull()?.toIntOrNull()

        print("転送先のホストを入力してください(未入力の場合はlocalhostになります):")
        val remoteHost = readlnOrNull()?.takeIf { it.isNotBlank() } ?: "localhost"

        print("転送先のポートを入力してください:")
        val remotePort = readlnOrNull()?.toIntOrNull()

        if (localPort == null || remotePort == null) {
            println("[入力エラー]受信ポートと転送先のポートには有効な数字を入力してください")
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
            sendServerInfo() // サーバー情報出力
            println()
            println("[サーバーソケット] サーバーソケット起動しました (受信ポート:${localPort} -> 転送先：${remoteHost}:${remotePort})")
            println()


            while (true) {
                val clientSocket = serverSocket.accept()
                println("[ポート転送] ${clientSocket.inetAddress} -> ${remoteHost}:$remotePort")

                // クライアントソケットとリモートソケットを処理する新しいスレッドを開始
                Thread(ForwardingHandler(clientSocket, remoteHost, remotePort)).start()
            }
        }
    } catch (e: Exception) {
        println("エラーが発生しました: ${e.message}")
    }
}

private fun sendServerInfo() {
    val localHost = InetAddress.getLocalHost()
    val privateIP = localHost.hostAddress // プライベートIP取得
    val apiURL = URL("https://api.ipify.org") // グローバルIPを取得するためのapiのURL
    val globalIP = apiURL.readText() // グローバルIPをapiで取得

    println("[サーバーソケット情報]")
    println("プライベートIP:$privateIP")
    println("グローバルIP:$globalIP")
}