package org.example

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import javax.sound.sampled.Port

class ForwardingHandler(private val clientSocket:Socket?,private val remoteHost:String?,private val remotePort:Int,private val localPort: Int):Runnable {

    fun release() {
        try {
            ServerSocket(localPort).use { serverSocket ->
                println("Listening on port $localPort")
                while (true) {
                    val clientSocket = serverSocket.accept()
                    println("Client connected: " + clientSocket.inetAddress)

                    // クライアントソケットとリモートソケットを処理する新しいスレッドを開始
                    Thread(this).start()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun run() {
        try {
            Socket(remoteHost, remotePort).use { remoteSocket ->
                clientSocket!!.getInputStream().use { clientIn ->
                    clientSocket.getOutputStream().use { clientOut ->
                        remoteSocket.getInputStream().use { remoteIn ->
                            remoteSocket.getOutputStream().use { remoteOut ->

                                // クライアントからリモートサーバーへのデータ転送
                                Thread { forwardData(clientIn, remoteOut) }.start()
                                // リモートサーバーからクライアントへのデータ転送
                                forwardData(remoteIn, clientOut)
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun forwardData(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(4096)
        var bytesRead: Int
        try {
            while ((`in`.read(buffer).also { bytesRead = it }) != -1) {
                out.write(buffer, 0, bytesRead)
                out.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}