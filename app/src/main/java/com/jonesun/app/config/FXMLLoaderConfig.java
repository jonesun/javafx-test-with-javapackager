//package com.jonesun.app.config;
//
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.JavaFXBuilderFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author jone.sun
// * 2021/5/10 14:58
// */
//@Configuration
//public class FXMLLoaderConfig {
//
//    @Autowired
//    protected ApplicationContext springContext;
//
//    @Bean
//    public FXMLLoader getFXMLLoader() {
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        FXMLLoader.setDefaultClassLoader(getClass().getClassLoader());
//        fxmlLoader.setClassLoader(getClass().getClassLoader());
//        fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory(getClass().getClassLoader()));
//        fxmlLoader.setControllerFactory(springContext::getBean);
//        return fxmlLoader;
//    }
//
//}
