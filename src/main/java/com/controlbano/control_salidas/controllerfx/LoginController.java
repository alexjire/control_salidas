package com.controlbano.control_salidas.controllerfx;

import com.controlbano.control_salidas.JavaFxApplication;
import com.controlbano.control_salidas.entity.Usuario;
import com.controlbano.control_salidas.repository.UsuarioRepository;
import com.controlbano.control_salidas.service.AuthService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private UsuarioRepository usuarioRepo;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize(){

        try{

            // Cargar usuarios
            List<String> usuarios =
                    usuarioRepo.findAll()
                            .stream()
                            .map(Usuario::getUsername)
                            .toList();

            comboUsuarios.setItems(
                    FXCollections.observableArrayList(usuarios)
            );

            // ⭐ ENTER funciona como click al botón
            btnEntrar.setDefaultButton(true);

            // ⭐ Enter en password también ejecuta login
            txtPassword.setOnAction(e -> login());

        }catch(Exception e){
            lblError.setText("Error cargando usuarios");
        }
    }

    // =====================================================
    // LOGIN
    // =====================================================

    @FXML
    public void login(){

        try{

            String username = comboUsuarios.getValue();
            String password = txtPassword.getText();

            if(username == null){
                lblError.setText("Seleccione usuario");
                return;
            }

            Usuario user;

            // Password opcional
            if(password == null || password.isEmpty()){
                user = authService.login(username, null);
            }else{
                user = authService.login(username, password);
            }

            // Guardar sesión global
            JavaFxApplication.usuarioLogueado = user.getUsername();
            JavaFxApplication.rolLogueado = user.getRol();

            // Navegación
            if(user.getRol().toUpperCase().contains("ADMIN")){
                JavaFxApplication.cambiarVista(
                        "/templates/dashboard.fxml",
                        true
                );
            }
            else{
                JavaFxApplication.cambiarVista(
                        "/templates/scanner.fxml",
                        true
                );
            }

        }catch(Exception e){
            lblError.setText(e.getMessage());
        }
    }
}