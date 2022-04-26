[Liberica JDK 不仅包含了 JavaFX，还继续为 Windows 以及 Linux 提供 32 位构建](https://bell-sw.com/pages/downloads/#/java-17-current)

为了运行javaFx，请下载Full version版本

安装[inno setup6](https://jrsoftware.org/isdl.php), 并加入到path中，用于制作windows安装程序(最好下载最新版本)
为了“iscc”这个命令行能够执行

安装[WiX Toolset](https://github.com/wixtoolset/wix3/releases/tag/wix3112rtm), 并将bin目录加入到path中，用于创建Windows安装包。
为了“candle”和"light"两个命令行能够执行

关于[JavaPackager](https://github.com/fvarrui/JavaPackager) 更多配置可参考其github

引入[update4j](https://github.com/update4j/update4j) 用于支持工具更新

## 使用前准备

使用nginx映射一个静态文件访问路径，或者其他方式，只要可以提供文件下载的服务就行，用于自动更新时下载对应配置和jar使用

找到bootstrap项目中的JavaFxDelegate类，修改configUrl为：xxx/app/config.xml

找到create-config项目中的CreateConfig类，修改BASE_URL为：xxx

使用idea或者其他工具运行maven命令

```
mvn package
```

对应会在项目的根目录下生成app-update文件夹，将内部的app文件夹上传到提供下载服务的目录中，打开浏览器访问: xxx/app/config.xml, 可以正常访问即可

找到bootstrap项目的target文件夹，可以发现bootstrap_1.0.0.exe、bootstrap_1.0.0.msi，打开即可安装，或者直接打开bootstrap文件中的bootstrap.exe运行免安装版

# 加解密

为了防止工具被任意分发，可以使用证书加解密功能

* 使用jdk提供的keytool创建keystore文件
```
keytool -genkeypair -alias jonesun.github.io -keyalg RSA -keystore D:\IdeaProjects\javafx-test-with-javapackager\jonesun.keystore -storetype pkcs12  -validity 1000
```

按照提示输入密码，demo中使用的是12345678，根据自己需求修改即可(注意文件keystore根据自己实际环境修改)

* create-config中加入加密功能

```
 KeyStore ks = KeyStore.getInstance("pkcs12");
try (FileInputStream in = new FileInputStream("jonesun.keystore")) {
    ks.load(in, "12345678".toCharArray());

    PrivateKey key = (PrivateKey) ks.getKey("jonesun.github.io", "12345678".toCharArray());
    System.out.println("key: " + key);

    Configuration config = Configuration.builder()
            .baseUri(BASE_URL + "/app")
            .basePath("${user.dir}/app")
            .files(refs)
            .property("maven.central", MAVEN_BASE)
            .signer(key)
            .build();
    
    //签名相关
    //https://github.com/update4j/update4j/issues/5
    try (Writer out = Files.newBufferedWriter(Paths.get(dir + "/config.xml"))) {
        config.write(out);
    }
}
```

* bootstrap加入解密功能

JavaFxDelegate类中:

```
URL configUrl = new URL("http://192.168.31.13/resource/demo/app/config.xml");
try (Reader in = new InputStreamReader(configUrl.openStream(), StandardCharsets.UTF_8);
     InputStream certIn = Files.newInputStream(Paths.get("jonesun.keystore"))) {
    KeyStore ks = KeyStore.getInstance("pkcs12");
    ks.load(certIn, "12345678".toCharArray());
    Certificate certificate = ks.getCertificate("jonesun.github.io");
    PublicKey publicKey = certificate.getPublicKey();
    Configuration config = Configuration.read(in, publicKey);

    StartupView startup = new StartupView(config, primaryStage);

    Scene scene = new Scene(startup);
    scene.getStylesheets().add(getClass().getResource("root.css").toExternalForm());

    primaryStage.getIcons().addAll(images);
    primaryStage.setScene(scene);

    primaryStage.setTitle("Update4j Demo Launcher");
    primaryStage.show();
} catch (IOException e) {
    e.printStackTrace();
    Optional<ButtonType>  buttonType = new Alert(Alert.AlertType.ERROR, "发生错误，请检查配置或者网络", new ButtonType[]{ButtonType.CLOSE}).showAndWait();
    buttonType.ifPresent(buttonType1 -> {
        Platform.exit();
        System.exit(0);
    });
}
```

pom.xml中加入将.keystore加入到工具中:

```xml
     <additionalResource>../jonesun.keystore</additionalResource>
```
# 注意

项目升级到java17后, 想要直接运行javafx打开弹框得话, 需要在idea得启动配置里添加vm参数

```
--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
--add-exports=javafx.base/com.sun.javafx.binding=ALL-UNNAMED
--add-exports=javafx.base/com.sun.javafx.event=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED
--add-exports=javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports=javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED
--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED
--add-opens=java.base/java.lang.reflect=ALL-UNNAMED
--add-opens=java.base/java.util=ALL-UNNAMED
```