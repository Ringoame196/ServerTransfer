package org.example

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

        val server = Server(localPort, remoteHost, remotePort)
        server.start()

        // ユーザー入力を監視して、"stop"コマンドを受け取る
        Thread {
            while (true) {
                val input = readlnOrNull()
                if (input.equals("stop", ignoreCase = true)) {
                    server.stop()
                    break
                }
            }
        }.start()
    }catch (e:NumberFormatException) {
        println("ポートは数字のみを入力してください")
    }
}