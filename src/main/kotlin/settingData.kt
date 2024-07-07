package org.example

import java.net.Socket

data class settingData(val clientSocket: Socket, val remoteHost:String, val remotePort:Int)

