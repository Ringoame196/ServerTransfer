package org.example

fun main() {
    val frameManager = FrameManager()
    val frame = frameManager.make() // フレームを作る
    frame.isVisible = true // フレームを表示する
}