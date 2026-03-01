package com.controlbano.control_salidas.controllerfx;

import com.controlbano.control_salidas.JavaFxApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import javafx.collections.FXCollections;
import javafx.stage.Stage;

import javafx.beans.property.SimpleStringProperty;

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

    /* ===================================================== */
    /* INITIALIZE */
    /* ===================================================== */

    @FXML
    public void initialize(){

        context = com.controlbano.control_salidas.JavaFxApplication.context;

        tablaRegistros.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        configurarTabla();
        aplicarColoresTabla();
        iniciarReloj();

        txtCarnet.textProperty().addListener((obs, oldValue, newValue) -> {
            if(newValue != null){
                txtCarnet.setText(newValue.toUpperCase());
            }
        });

        txtCarnet.setOnAction(event -> procesarEscaneo());

        cargarTabla();
    }

    /* ===================================================== */
    /* RELOJ PROFESIONAL */
    /* ===================================================== */

    private void iniciarReloj(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss",
                        Locale.forLanguageTag("es"));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> {
                    lblReloj.setText(LocalDateTime.now().format(formatter));
                })
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /* ===================================================== */
    /* MOSTRAR MENSAJE PROFESIONAL */
    /* ===================================================== */

    private void mostrarMensaje(String mensaje){

        if(mensaje == null || mensaje.isEmpty()){
            lblResultado.setText("");
            lblResultado.setStyle("");
            return;
        }

        lblResultado.setText(mensaje);

        lblResultado.setStyle("""
            -fx-background-color: rgba(255,255,255,0.08);
            -fx-background-radius: 18;
            -fx-effect: dropshadow(gaussian,
            rgba(0,0,0,0.5),
            20, 0.3, 0, 8);
            -fx-padding: 12 25;
        """);
    }

    /* ===================================================== */
    /* LIMPIAR MENSAJE AUTOMATICO */
    /* ===================================================== */

    private void limpiarMensajeAutomatico(){

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        e -> mostrarMensaje(""))
        );

        timeline.play();
    }

    /* ===================================================== */
    /* PROCESAR ESCANEO */
    /* ===================================================== */

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

            mostrarMensaje(
                    "Carnet: " + carnet +
                            " | Empleado: " + nombre +
                            " | " + tipo
            );

            limpiarMensajeAutomatico();

            txtCarnet.clear();
            cargarTabla();

        } catch (Exception e){
            mostrarMensaje("Empleado no encontrado");
            limpiarMensajeAutomatico();
        }
    }

    /* ===================================================== */
    /* TABLA DATA */
    /* ===================================================== */

    private void cargarTabla(){

        tablaRegistros.setItems(
                FXCollections.observableArrayList(
                        service.obtenerRegistrosDelDia()
                )
        );
    }

    /* ===================================================== */
    /* CONFIGURAR TABLA */
    /* ===================================================== */

    private void configurarTabla(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("h:mm a",
                        Locale.forLanguageTag("es"));

        colCarnet.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEmpleado().getCarnet()));

        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEmpleado().getNombre()));

        colSalida.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getHoraSalida()!=null ?
                                data.getValue().getHoraSalida().format(formatter) : ""));

        colEntrada.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getHoraEntrada()!=null ?
                                data.getValue().getHoraEntrada().format(formatter) : ""));

        colDuracion.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getDuracionMinutos()!=null ?
                                data.getValue().getDuracionMinutos()+" min" : ""));

        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getEstado()));
    }

    /* ===================================================== */
    /* COLORES TABLA */
    /* ===================================================== */

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

    /* ===================================================== */
    /* LOGOUT */
    /* ===================================================== */

    @FXML
    public void salirSistema(){
        JavaFxApplication.cambiarVista(
                "/templates/login.fxml",
                false
        );
    }
}