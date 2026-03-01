package com.controlbano.control_salidas.controllerfx;

import com.controlbano.control_salidas.JavaFxApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import com.controlbano.control_salidas.service.AuthService;
import com.controlbano.control_salidas.entity.Usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ConfigurableApplicationContext;

import javafx.collections.FXCollections;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.util.List;

@Component
public class LoginController {

    @FXML private ComboBox<String> comboUsuarios;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnEntrar;

    @Autowired
    private AuthService authService;

    @Autowired
    private com.controlbano.control_salidas.repository.UsuarioRepository usuarioRepo;

    private ConfigurableApplicationContext context;

    @FXML
    public void initialize() {

        try {

            List<String> usuarios =
                    usuarioRepo.findAll()
                            .stream()
                            .map(Usuario::getUsername)
                            .toList();

            comboUsuarios.setItems(
                    FXCollections.observableArrayList(usuarios)
            );

            context = com.controlbano.control_salidas.JavaFxApplication.context;

            btnEntrar.setDefaultButton(true);

            txtPassword.setOnAction(e -> login());

        } catch (Exception e) {
            lblError.setText("Error cargando usuarios");
        }
    }

    @FXML
    public void login() {

        try {

            String username = comboUsuarios.getValue();
            String password = txtPassword.getText();

            if(username == null){
                lblError.setText("Seleccione usuario");
                return;
            }

            Usuario user;

        /*
        ⭐ Si el password está vacío → mandar null al service
        */
            if(password == null || password.isEmpty()){
                user = authService.login(username, null);
            }
            else{
                user = authService.login(username, password);
            }

            String rol = user.getRol().toUpperCase();
            JavaFxApplication.usuarioLogueado = user.getUsername();
            JavaFxApplication.rolLogueado = user.getRol();

            boolean esAdmin = rol.contains("ADMIN");

            String view = esAdmin
                    ? "/templates/dashboard.fxml"
                    : "/templates/scanner.fxml";

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(view));

            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();

            Stage stage = (Stage) comboUsuarios.getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            lblError.setText(e.getMessage());
            e.printStackTrace();
        }
    }
}