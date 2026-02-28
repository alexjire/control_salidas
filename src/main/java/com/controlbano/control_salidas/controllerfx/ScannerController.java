package com.controlbano.control_salidas.controllerfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import org.springframework.context.ConfigurableApplicationContext;

import com.controlbano.control_salidas.service.RegistroBanioService;
import com.controlbano.control_salidas.entity.RegistroBanio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class ScannerController {

    @FXML private TextField txtCarnet;
    @FXML private Label lblResultado;

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

        configurarTabla();
        aplicarColoresTabla();

        txtCarnet.setOnAction(event -> procesarEscaneo());

        cargarTabla();
    }

    private void procesarEscaneo(){

        try {

            String carnet = txtCarnet.getText().trim();
            if(carnet.isEmpty()) return;

            RegistroBanio registro = service.procesarEscaneo(carnet);

            mostrarMensaje(registro);

            txtCarnet.clear();

            cargarTabla();

        } catch (Exception e){
            lblResultado.setText("Empleado no encontrado");
        }
    }

    private void mostrarMensaje(RegistroBanio registro){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("h:mm a / dd MMM yyyy",
                        Locale.forLanguageTag("es"));

        String hora = "";

        if(registro.getHoraEntrada()!=null){
            hora = registro.getHoraEntrada().format(formatter);
        }
        else if(registro.getHoraSalida()!=null){
            hora = registro.getHoraSalida().format(formatter);
        }

        String tipo =
                registro.getHoraEntrada()==null ?
                        "Marca de Salida registrada" :
                        "Marca de Entrada registrada";

        String texto =
                "✔ Empleado verificado\n\n" +
                        registro.getEmpleado().getNombre() + "\n" +
                        registro.getEmpleado().getCarnet() + "\n\n" +
                        tipo + "\n" +
                        "Fecha y hora: " + hora;

        lblResultado.setText(texto);

        new Thread(() -> {
            try {
                Thread.sleep(4000);
                Platform.runLater(() -> lblResultado.setText(""));
            } catch (InterruptedException ignored){}
        }).start();
    }

    private void cargarTabla(){

        try{

            tablaRegistros.setItems(
                    FXCollections.observableArrayList(
                            service.obtenerRegistrosDelDia()
                    )
            );

        }catch (Exception ignored){}
    }

    private void configurarTabla(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("h:mm a / dd MMM yyyy",
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
                    setStyle("-fx-background-color:#c8e6c9;");
                }
                else if("ABIERTO".equals(item.getEstado())){
                    setStyle("-fx-background-color:#fff9c4;");
                }
                else{
                    setStyle("-fx-background-color:#ffcdd2;");
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