package org.example.Data

import java.net.Socket

data class SettingData(val clientSocket: Socket, val remoteHost:String, val remotePort:Int)