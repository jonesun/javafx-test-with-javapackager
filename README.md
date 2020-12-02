[Liberica JDK 不仅包含了 JavaFX，还继续为 Windows 以及 Linux 提供 32 位构建](https://bell-sw.com/pages/downloads/#/java-15-current)

为了运行javaFx，请下载Full version版本

安装[inno setup6](https://www.microsoft.com/zh-CN/download/details.aspx?id=21), 并加入到path中，用于制作windows安装程序(最好下载最新版本)
为了“iscc”这个命令行能够执行

安装[WiX Toolset](https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm), 并将bin目录加入到path中，用于创建Windows安装包。
为了“candle”和"light"两个命令行能够执行

关于[JavaPackager](https://github.com/fvarrui/JavaPackager)更多配置可参考其github

制作安装程序, 使用

```
mvn package
```