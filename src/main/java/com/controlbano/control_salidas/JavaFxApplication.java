package com.controlbano.control_salidas;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ConfigurableApplicationContext;

import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class JavaFxApplication extends Application {

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

    /*
    ⭐ Inicializar Spring Context
     */
    @Override
    public void init(){
        context = SpringApplication.run(
                JavaFxApplication.class);
    }

    /*
    ⭐ Cache Manager
     */
    @Bean
    public org.springframework.cache.CacheManager cacheManager(){
        return new ConcurrentMapCacheManager("empleados");
    }

    /*
    ⭐ Password Encoder Seguridad Profesional
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /*
    ⭐ JavaFX View Launcher
     */
    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/templates/login.fxml"));

        loader.setControllerFactory(context::getBean);

        Parent root = loader.load();

        stage.setScene(new Scene(root,800,600));
        stage.setTitle("Control de Salidas al Baño");
        stage.show();
    }
}