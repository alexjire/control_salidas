package com.controlbano.control_salidas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class JavaFxApplication extends Application {

    public static ConfigurableApplicationContext context;

    private static Stage primaryStage;

    public static String usuarioLogueado;
    public static String rolLogueado;

    public static void main(String[] args){
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
    public void start(Stage stage){

        primaryStage = stage;

        cambiarVista("/templates/login.fxml", false);

        primaryStage.setTitle("Control de Salidas al Baño");
        primaryStage.show();
    }

    // ⭐ NAVIGATION GLOBAL
    public static void cambiarVista(String fxml, boolean maximizar){

        try{

            FXMLLoader loader =
                    new FXMLLoader(
                            JavaFxApplication.class.getResource(fxml));

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            Scene scene = new Scene(root);

            primaryStage.setScene(scene);

            primaryStage.setFullScreen(false);
            primaryStage.setResizable(true);

            if(maximizar){
                primaryStage.setMaximized(true);
            }else{
                primaryStage.setMaximized(false);

                primaryStage.setWidth(420);
                primaryStage.setHeight(520);
            }

            primaryStage.centerOnScreen();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}