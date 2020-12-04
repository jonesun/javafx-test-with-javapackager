package com.jonesun.app.service;

import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Component;

/**
 * @author jone.sun
 * @date 2020-12-04 18:16
 */
@Component
public class MyService {

    public String getSpringVersion() {
        return "spring: "+ SpringVersion.getVersion() + ", springboot: " + SpringBootVersion.getVersion();
    }
}
