package com.jonesun.app;

import com.jonesun.app.service.MyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jone.sun
 * @date 2020-12-04 11:07
 */
@Component
public class Controller implements Initializable {

    @FXML
    public Label label;

    @Autowired
    MyService myService;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        label.setText("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + "."
                + "\r\n" + myService.getSpringVersion());
    }
}
