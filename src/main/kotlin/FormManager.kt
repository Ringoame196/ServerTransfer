package org.example

import javax.swing.GroupLayout
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JTextField

class FormManager {
    fun makeTopForm(): JFrame {
        val frame = JFrame("ServerTransfer")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(400, 200)

        // テキストボックスの作成
        val textFieldIP = JTextField(15)
        val textFieldReceptionPort = JTextField(5)
        val textFieldForwardPort = JTextField(5)

        // テキストボックスにデフォルト入力する
        textFieldIP.text = "localhost"

        // ラベルの作成
        val labelIP = JLabel("転送先IP:")
        val labelReceptionPort = JLabel("受付ポート:")
        val labelForwardPort = JLabel("転送先ポート:")

        // ボタンの作成
        val button = JButton("公開")

        // レイアウトの設定
        val layout = GroupLayout(frame.contentPane)
        frame.layout = layout
        layout.autoCreateGaps = true
        layout.autoCreateContainerGaps = true

        // 水平方向のグループ
        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(labelIP)
                        .addComponent(labelReceptionPort)
                        .addComponent(labelForwardPort)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(textFieldIP)
                        .addComponent(textFieldReceptionPort)
                        .addComponent(textFieldForwardPort)
                        .addComponent(button)
                )
        )

        // 垂直方向のグループ
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelIP)
                        .addComponent(textFieldIP)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelReceptionPort)
                        .addComponent(textFieldReceptionPort)
                )
                .addGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(labelForwardPort)
                        .addComponent(textFieldForwardPort)
                )
                .addComponent(button)
        )

        // ボタンのアクションを設定
        button.addActionListener {
            val ip = textFieldIP.text
            val receptionPort = textFieldReceptionPort.text
            val forwardPort = textFieldForwardPort.text
            JOptionPane.showMessageDialog(
                frame,
                "設定内容:\n転送先IP: $ip\n受付ポート: $receptionPort\n転送先ポート: $forwardPort"
            )
        }
        return frame
    }
}