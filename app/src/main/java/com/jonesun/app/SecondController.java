package com.jonesun.app;

import com.jonesun.app.service.MyService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jone.sun
 * 2021/5/8 17:47
 */
@Lazy
@Controller
public class SecondController implements Initializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @FXML
    public Label label;

    @Autowired
    MyService myService;

    @PostConstruct
    public void init() {
        logger.info("init");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("SecondController: " + myService.getSpringVersion());
        logger.info("hello");
    }
}
