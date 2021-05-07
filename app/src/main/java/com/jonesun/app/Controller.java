package com.jonesun.app;

import com.jonesun.app.service.MyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jone.sun
 * @date 2020-12-04 11:07
 */
@Component
public class Controller implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public Label label;

    @Autowired
    MyService myService;

    //--spring.profiles.active=test
    @Value("${spring.application.name:1111}")
    private String applicationName;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + "."
                + "\r\n" + myService.getSpringVersion()
                + "\r\n applicationName is " + applicationName);
        logger.debug("debug test");
        logger.info("info test");
        logger.error("error test");
        logger.warn("warn test");
        logger.trace("trace test");

    }
}
