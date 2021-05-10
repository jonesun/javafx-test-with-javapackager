package com.jonesun.app;

import com.jonesun.app.service.MyService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
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

    @Autowired
    private ConfigurableApplicationContext springContext;


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


    public void doOpenNewPage(ActionEvent actionEvent) throws IOException {
        //todo 这种方式打包后用启动器启动会报ClassNotFoundException 后期看看如何解决
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/second.fxml"));
//        fxmlLoader.setControllerFactory(springContext::getBean);
//        getScene(actionEvent).setRoot(fxmlLoader.load());
//        secondController.initialize(null, null);

        Scene scene = getScene(actionEvent);
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/second.fxml"));
//        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
//            fxmlLoader.setRoot(this);
        fxmlLoader.setController(springContext.getBean(SecondController.class));

        fxmlLoader.setControllerFactory(springContext::getBean);
        scene.setRoot(fxmlLoader.load());
    }

    public Scene getScene(ActionEvent actionEvent) {
        return ((Node) actionEvent.getSource()).getScene();
    }
}
