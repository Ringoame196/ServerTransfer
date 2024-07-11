package org.example

import data.SettingData
import org.example.common.ForwardingHandler
import org.example.common.LogManager
import java.net.InetAddress
import java.net.ServerSocket
import java.net.URL

private val logManager = LogManager()

fun main() {
    Runtime.getRuntime().addShutdownHook(Thread {
        logManager.sendLog("サーバーシャットダウン")
    })

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
        val whitelist = mutableListOf<String>()
        while (true) {
            print("通信を許可するIPを入力してください(未入力で設定完了 登録しない場合は全て許可)：")
            val ip = readlnOrNull()?.takeIf { it.isNotBlank() }
            if (ip == null) {
                break
            } else {
                whitelist.add(ip)
            }
        }

        println()
        val settingData = SettingData(localPort,remoteHost,remotePort)

        startServerSocket(settingData,whitelist) // サーバーソケットを起動
    }catch (e:NumberFormatException) {
        println("ポートは数字のみを入力してください")
    }
}

private fun startServerSocket(settingData: SettingData,whitelist:MutableList<String>) {
    try {
        val localPort = settingData.localPort
        val remoteHost = settingData.remoteHost
        val remotePort = settingData.remotePort
        ServerSocket(localPort).use { serverSocket ->
            sendServerInfo() // サーバー情報出力
            println()
            val startMessage = "[サーバーソケット] サーバーソケット起動しました (受信ポート:${localPort} -> 転送先：${remoteHost}:${remotePort})"
            logManager.sendLog(startMessage)

            while (true) {
                val clientSocket = serverSocket.accept()

                val ipAddress = clientSocket.inetAddress

                if (checkCommunicationBlock(ipAddress.toString(),whitelist) && whitelist.size > 0) {
                    println("許可されたIP以外からの通信のため遮断しました")
                    val transferMessage = "[ポート転送] ${ipAddress}:${clientSocket.port} -> ブロック"
                    logManager.sendLog(transferMessage)
                    clientSocket.close()
                } else {
                    val transferMessage = "[ポート転送] ${ipAddress}:${clientSocket.port} -> ${remoteHost}:$remotePort"
                    logManager.sendLog(transferMessage)
                    // クライアントソケットとリモートソケットを処理する新しいスレッドを開始
                    Thread(ForwardingHandler(clientSocket, settingData)).start()
                }
            }
        }
    } catch (e: Exception) {
        val errorMessage = "エラーが発生しました: ${e.message}"
        logManager.sendLog(errorMessage)
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

private fun checkCommunicationBlock(ipAddress:String, whitelist:MutableList<String>):Boolean {
    return !whitelist.contains(ipAddress.replace("/","")) // ホワイトリストチェック
}