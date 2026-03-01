package com.controlbano.control_salidas.controllerfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.controlbano.control_salidas.service.RegistroBanioService;
import com.controlbano.control_salidas.entity.RegistroBanio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ScannerController {

    @FXML private TextField txtCarnet;
    @FXML private Label lblResultado;
    @FXML private Label lblReloj;

    @FXML private TableView<RegistroBanio> tablaRegistros;

    @FXML private TableColumn<RegistroBanio,String> colCarnet;
    @FXML private TableColumn<RegistroBanio,String> colNombre;
    @FXML private TableColumn<RegistroBanio,String> colSalida;
    @FXML private TableColumn<RegistroBanio,String> colEntrada;
    @FXML private TableColumn<RegistroBanio,String> colDuracion;
    @FXML private TableColumn<RegistroBanio,String> colEstado;

    @Autowired
    private RegistroBanioService service;

    private ConfigurableApplicationContext context;

    @FXML
    public void initialize(){

        context = com.controlbano.control_salidas.JavaFxApplication.context;

        tablaRegistros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configurarTabla();
        aplicarColoresTabla();
        iniciarReloj();

        // 🔥 Siempre mostrar carnet en MAYÚSCULA
        txtCarnet.textProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                txtCarnet.setText(newValue.toUpperCase());
            }
        });

        txtCarnet.setOnAction(event -> procesarEscaneo());

        cargarTabla();
    }

    private void iniciarReloj(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss",
                        Locale.forLanguageTag("es"));

        Thread relojThread = new Thread(() -> {
            try{
                while(true){
                    LocalDateTime ahora = LocalDateTime.now();
                    Platform.runLater(() ->
                            lblReloj.setText(ahora.format(formatter)));
                    Thread.sleep(1000);
                }
            }catch (Exception ignored){}
        });

        relojThread.setDaemon(true);
        relojThread.start();
    }

    private void procesarEscaneo(){

        try {

            String carnet = txtCarnet.getText().trim().toUpperCase();
            if(carnet.isEmpty()) return;

            RegistroBanio registro = service.procesarEscaneo(carnet);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("hh:mm a",
                            Locale.forLanguageTag("es"));

            String nombre = registro.getEmpleado().getNombre();
            String tipo;

            if(registro.getHoraEntrada() == null){
                tipo = "SALIDA registrada a las " +
                        registro.getHoraSalida().format(formatter);
            }else{
                tipo = "ENTRADA registrada a las " +
                        registro.getHoraEntrada().format(formatter);
            }

            lblResultado.setText(
                    "Carnet: " + carnet +
                            " | Empleado: " + nombre +
                            " | " + tipo
            );

            // 🔥 mensaje desaparece en 5 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> lblResultado.setText(""));
                } catch (InterruptedException ignored) {}
            }).start();

            txtCarnet.clear();
            cargarTabla();

        } catch (Exception e){
            lblResultado.setText("Empleado no encontrado");
        }
    }

    private void cargarTabla(){
        tablaRegistros.setItems(
                FXCollections.observableArrayList(
                        service.obtenerRegistrosDelDia()
                )
        );
    }

    private void configurarTabla(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("h:mm a",
                        Locale.forLanguageTag("es"));

        colCarnet.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEmpleado().getCarnet()));

        colNombre.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEmpleado().getNombre()));

        colSalida.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraSalida()!=null ?
                                data.getValue().getHoraSalida().format(formatter) : ""));

        colEntrada.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getHoraEntrada()!=null ?
                                data.getValue().getHoraEntrada().format(formatter) : ""));

        colDuracion.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDuracionMinutos()!=null ?
                                data.getValue().getDuracionMinutos()+" min" : ""));

        colEstado.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getEstado()));
    }

    private void aplicarColoresTabla(){

        tablaRegistros.setRowFactory(tv -> new TableRow<>() {

            @Override
            protected void updateItem(RegistroBanio item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty){
                    setStyle("");
                    return;
                }

                if("CERRADO".equals(item.getEstado())){
                    setStyle("-fx-background-color:#E8F5E9;");
                }
                else if("ABIERTO".equals(item.getEstado())){
                    setStyle("-fx-background-color:#FFFDE7;");
                }
                else{
                    setStyle("-fx-background-color:#FFEBEE;");
                }
            }
        });
    }

    @FXML
    public void salirLogin(){
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/templates/login.fxml"));
            loader.setControllerFactory(
                    com.controlbano.control_salidas.JavaFxApplication.context::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) txtCarnet.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }catch (Exception ignored){}
    }
}