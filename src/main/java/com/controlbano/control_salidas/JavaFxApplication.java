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

    public static String usuarioLogueado;
    public static String rolLogueado;

    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init(){
        context = SpringApplication.run(JavaFxApplication.class);
    }

    @Bean
    public org.springframework.cache.CacheManager cacheManager(){
        return new ConcurrentMapCacheManager("empleados");
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/templates/login.fxml"));

        loader.setControllerFactory(context::getBean);

        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Control de Salidas al Baño");

        // 🔥 PANTALLA COMPLETA PROFESIONAL
        stage.setMaximized(true);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint(""); // quita mensaje ESC
        stage.setFullScreenExitKeyCombination(null); // bloquea ESC

        stage.show();
    }
}