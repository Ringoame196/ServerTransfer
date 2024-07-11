# ポート転送ソフト

## 機能
指定したポートに来た通信を別のポート、別のホストに通信を転送することができる

## 使い方
コマンドプロンプト、シェルで「java -jar ServerTransfer.jar」で起動 <br>

「受信ポート」、「転送先のホスト名」、「転送先のポート」を入力することで起動<br>
TCPの通信を転送することが可能になる

![image](https://github.com/Ringoame196/ServerTransfer/assets/132573268/1c90073c-f880-4133-b505-07bedc719c07)

## ログ
鯖起動ログや接続ログは./log/日にち.logファイルに追加されます

## 開発環境
- IntelliJ
- 言語：Kotlin<br>
- OpenJDK：17

## 使用API
- [Ipify](https://api.ipify.org) - グローバルIP取得
- [IP Geolocation API](https://ipwhois.io) - IP情報を取得
