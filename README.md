[Liberica JDK 不仅包含了 JavaFX，还继续为 Windows 以及 Linux 提供 32 位构建](https://bell-sw.com/pages/downloads/#/java-15-current)

为了运行javaFx，请下载Full version版本

安装[inno setup6](https://www.microsoft.com/zh-CN/download/details.aspx?id=21), 并加入到path中，用于制作windows安装程序(最好下载最新版本)
为了“iscc”这个命令行能够执行

安装[WiX Toolset](https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm), 并将bin目录加入到path中，用于创建Windows安装包。
为了“candle”和"light"两个命令行能够执行

关于[JavaPackager](https://github.com/fvarrui/JavaPackager)更多配置可参考其github

引用[update4j](https://github.com/update4j/update4j)用于支持工具更新

使用前准备

使用nginx映射一个静态文件访问路径，或者其他方式，只要可以提供文件下载的服务就行，用于自动更新时下载对应配置和jar使用

找到bootstrap项目中的JavaFxDelegate类，修改configUrl为：xxx/app/config.xml

找到create-config项目中的CreateConfig类，修改BASE_URL为：xxx

使用idea或者其他工具运行maven命令

```
mvn package
```

对应会在项目的根目录下生成app-update文件夹，将内部的app文件夹上传到提供下载服务的目录中，打开浏览器访问: xxx/app/config.xml, 可以正常访问即可

找到bootstrap项目的target文件夹，可以发现bootstrap_1.0.0.exe、bootstrap_1.0.0.msi，打开即可安装，或者直接打开bootstrap文件中的bootstrap.exe运行免安装版



