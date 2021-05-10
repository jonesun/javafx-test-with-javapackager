package com.jonesun.app.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXDecorator;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author jone.sun
 * 2021/5/8 10:02
 */
public abstract class BaseController implements Initializable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected ApplicationContext springContext;


    /**
     * 获取Scene
     *
     * @param actionEvent
     * @return
     */
    public Scene getScene(ActionEvent actionEvent) {
        return ((Node) actionEvent.getSource()).getScene();
    }

    /**
     * 获取Stage
     *
     * @param actionEvent
     * @return
     */
    public Stage getStage(ActionEvent actionEvent) {
        Scene scene = getScene(actionEvent);
        return (Stage) scene.getWindow();
    }

    /**
     * 获取Stage
     * 注意如果是在initialize中获取的话需使用 Platform.runLater(() -> Stage stage = getStage(node);});
     *
     * @param node
     * @return
     */
    public Stage getStage(Node node) {
        return (Stage) node.getScene().getWindow();
    }

    /**
     * 打开页面
     *
     * @param actionEvent
     * @param fxmlName
     * @throws IOException
     */
    public void openController(ActionEvent actionEvent, String fxmlName) throws IOException {
        Scene scene = getScene(actionEvent);
        Parent sceneRoot = scene.getRoot();
        if(sceneRoot instanceof JFXDecorator) {
            ((JFXDecorator)scene.getRoot()).setContent(getPaneByFxmlName(fxmlName));
        } else {
            scene.setRoot(getPaneByFxmlName(fxmlName));
        }
    }

    /**
     * 从fxml中获取布局
     * @param fxmlName
     * @return
     * @throws IOException
     */
    public Parent getPaneByFxmlName(String fxmlName) throws IOException {
        return FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlName)), null, null, springContext::getBean, StandardCharsets.UTF_8);
    }

    /**
     * 打开弹框形式的页面
     *
     * @param ownerStage
     * @param title
     * @param root
     */
    public <T> JFXAlert<T> openDialogController(Stage ownerStage, String title, Parent root) {
        //todo 后续可以做成单例，避免弹出多个JFXDialog
        JFXAlert<T> alert = new JFXAlert<>(ownerStage);
        alert.setTitle(title);
        alert.setContent(root);
//        //不允许点击其他区域关闭弹框
//        alert.setOverlayClose(false);
        alert.show();
        return alert;
    }

    /**
     * 打开弹框形式的页面
     *
     * @param actionEvent
     * @param title
     * @param root
     */
    public <T> JFXAlert<T> openDialogController(ActionEvent actionEvent, String title, Parent root) {
        return openDialogController(getStage(actionEvent), title, root);
    }
}
