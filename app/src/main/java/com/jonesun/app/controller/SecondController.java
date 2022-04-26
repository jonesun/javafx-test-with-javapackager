package com.jonesun.app.controller;

import com.jonesun.app.service.MyService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author jone.sun
 * 2021/5/10 14:50
 */
@Lazy //采用懒加载，只有用到该页面才初始化该对象
@Controller
public class SecondController extends BaseController {

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
        logger.info("initialize");
        label.setText(myService.getSpringVersion());
    }

    public void doGoBack(ActionEvent actionEvent) throws IOException {
        Stage stage = getStage(actionEvent);
        if (stage.getOwner() != null) {
            stage.close();
            stage = (Stage) stage.getOwner();
        }
        openController(stage.getScene(), "/fxml/sample.fxml");
    }
}
