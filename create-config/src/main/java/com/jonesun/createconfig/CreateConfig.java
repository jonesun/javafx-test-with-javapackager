package com.jonesun.createconfig;

import org.update4j.Configuration;
import org.update4j.FileMetadata;
import org.update4j.OS;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CreateConfig {

    //https://repo1.maven.org/maven2
    private static final String MAVEN_BASE = "http://maven.aliyun.com/nexus/content/groups/public";

    private static final String BASE_URL = "http://192.168.31.13/resource/demo";

    public static void main(String[] args) throws IOException {


        String configLoc = System.getProperty("config.location");

        String dir = configLoc + "/app";

        File[] jfiles = new File(dir).listFiles();
        List<FileMetadata.Reference> refs = new ArrayList<>();
        for (File file: jfiles) {
            if (!file.getName().endsWith("jar")) {
                // do nth
                // contains("config.xml")
                continue;
            } else if (file.getName().contains("jul-to-slf4j")) {
                refs.add(FileMetadata.readFrom(file.getPath()).path(file.getName()).classpath().ignoreBootConflict());
            } else {
                refs.add(FileMetadata.readFrom(file.getPath()).path(file.getName()).classpath());
            }
        }

        Configuration config = Configuration.builder()
                .baseUri(BASE_URL + "/app")
                .basePath("${user.dir}/app")
                .files(refs)
//                        .file(FileMetadata.readFrom(dir + "/app-1.0.0.jar").path("app-1.0.0.jar").classpath())
//                        .file(FileMetadata.readFrom(dir + "/controlsfx-9.0.0.jar")
//                                        .uri(mavenUrl("org.controlsfx", "controlsfx", "9.0.0"))
//                                        .classpath())
//                        .file(FileMetadata.readFrom(dir + "/jfoenix-9.0.8.jar")
//                                        .uri(mavenUrl("com.jfoenix", "jfoenix", "9.0.8"))
//                                        .classpath())
//                        .file(FileMetadata.readFrom(dir + "/jfxtras-common-10.0-r1.jar")
//                                        .uri(mavenUrl("org.jfxtras", "jfxtras-common", "10.0-r1"))
//                                        .classpath())
//                        .file(FileMetadata.readFrom(dir + "/jfxtras-gauge-linear-10.0-r1.jar")
//                                        .uri(mavenUrl("org.jfxtras", "jfxtras-gauge-linear", "10.0-r1"))
//                                        .classpath())
                .property("maven.central", MAVEN_BASE)
                .build();

        try (Writer out = Files.newBufferedWriter(Paths.get(dir + "/config.xml"))) {
            config.write(out);
        }
    }
}
