package com.controlbano.control_salidas.controllerfx;

import com.controlbano.control_salidas.JavaFxApplication;
import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.service.RegistroBanioService;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableRow;
import javafx.util.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    // ⭐ Timeline único para limpiar mensaje
    private Timeline limpiarTimeline;

    // =====================================================
    // INITIALIZE
    // =====================================================

    @FXML
    public void initialize(){

        configurarTabla();
        aplicarColoresTabla();
        iniciarReloj();

        // ⭐ Enter ejecuta escaneo
        txtCarnet.setOnAction(e -> procesarEscaneo());

        cargarTabla();
    }

    // =====================================================
    // RELOJ
    // =====================================================

    private void iniciarReloj(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss",
                        Locale.forLanguageTag("es"));

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1),
                        e -> lblReloj.setText(
                                LocalDateTime.now().format(formatter)))
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // =====================================================
    // MENSAJES PROFESIONALES
    // =====================================================

    private void mostrarMensaje(String mensaje){

        if(mensaje == null || mensaje.isEmpty()){
            lblResultado.setText("");
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

    // =====================================================
    // LIMPIAR MENSAJE AUTOMÁTICO (5s)
    // =====================================================

    private void limpiarMensajeAutomatico(){

        if(limpiarTimeline != null){
            limpiarTimeline.stop();
        }

        limpiarTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5),
                        e -> mostrarMensaje(""))
        );

        limpiarTimeline.setCycleCount(1);
        limpiarTimeline.play();
    }

    // =====================================================
    // PROCESAR ESCANEO
    // =====================================================

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

    // =====================================================
    // TABLA DATA
    // =====================================================

    private void cargarTabla(){

        tablaRegistros.setItems(
                FXCollections.observableArrayList(
                        service.obtenerRegistrosDelDia()
                )
        );
    }

    // =====================================================
    // CONFIGURAR TABLA
    // =====================================================

    private void configurarTabla(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("h:mm a",
                        Locale.forLanguageTag("es"));

        colCarnet.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmpleado().getCarnet()));

        colNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmpleado().getNombre()));

        colSalida.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getHoraSalida()!=null ?
                                d.getValue().getHoraSalida().format(formatter) : ""));

        colEntrada.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getHoraEntrada()!=null ?
                                d.getValue().getHoraEntrada().format(formatter) : ""));

        colDuracion.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getDuracionMinutos()!=null ?
                                d.getValue().getDuracionMinutos()+" min" : ""));

        colEstado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEstado()));
    }

    // =====================================================
    // COLORES TABLA
    // =====================================================

    private void aplicarColoresTabla(){

        tablaRegistros.setRowFactory(tv -> new TableRow<>() {

            @Override
            protected void updateItem(RegistroBanio item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null || empty) return;

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

    // =====================================================
    // LOGOUT
    // =====================================================

    @FXML
    public void salirLogin(){
        JavaFxApplication.cambiarVista(
                "/templates/login.fxml",
                false
        );
    }
}