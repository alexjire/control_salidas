package com.controlbano.control_salidas.controllerfx.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.controlbano.control_salidas.repository.RegistroBanioRepository;
import com.controlbano.control_salidas.repository.LineaRepository;
import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.service.PdfService;
import com.controlbano.control_salidas.JavaFxApplication;

import javafx.application.Platform;
import javafx.collections.FXCollections;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
public class DashboardController {

    @FXML private ComboBox<String> comboLineas;
    @FXML private DatePicker datePickerFecha;
    @FXML private TextField txtBuscarEmpleado;
    @FXML private TableView<RegistroBanio> tablaRegistros;

    @FXML private TableColumn<RegistroBanio,String> colCarnet;
    @FXML private TableColumn<RegistroBanio,String> colNombre;
    @FXML private TableColumn<RegistroBanio,String> colSalida;
    @FXML private TableColumn<RegistroBanio,String> colEntrada;
    @FXML private TableColumn<RegistroBanio,String> colDuracion;
    @FXML private TableColumn<RegistroBanio,String> colEstado;

    @FXML private Label lblReloj;

    @Autowired private RegistroBanioRepository registroRepo;
    @Autowired private PdfService pdfService;
    @Autowired private LineaRepository lineaRepo;

    private Thread relojThread;

    @FXML
    public void initialize(){

        System.out.println("Rol logueado: " + JavaFxApplication.rolLogueado);

        datePickerFecha.setValue(LocalDate.now());

        cargarLineas();
        configurarTabla();

        iniciarReloj();
        controlarVisibilidadReloj();

        comboLineas.setOnAction(e -> cargarTabla());
        datePickerFecha.setOnAction(e -> cargarTabla());

        txtBuscarEmpleado.textProperty()
                .addListener((obs, oldVal, newVal) -> cargarTabla());
    }

    // ===========================
    // VISIBILIDAD RELOJ
    // ===========================
    private void controlarVisibilidadReloj(){
        lblReloj.setVisible(true);
        lblReloj.setManaged(true);
    }

    // ===========================
    // RELOJ EN TIEMPO REAL
    // ===========================
    private void iniciarReloj(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "dd/MM/yyyy HH:mm:ss",
                        Locale.forLanguageTag("es"));

        relojThread = new Thread(() -> {

            try{

                while(!Thread.currentThread().isInterrupted()){

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

    // ===========================
    // CARGAR LINEAS
    // ===========================
    private void cargarLineas(){

        comboLineas.setItems(
                FXCollections.observableArrayList(
                        lineaRepo.findAll()
                                .stream()
                                .filter(l -> l.isActiva())
                                .map(l -> l.getNombre())
                                .toList()
                )
        );
    }

    // ===========================
    // CARGAR TABLA
    // ===========================
    @FXML
    public void cargarTabla(){

        if(comboLineas.getValue()==null)
            return;

        String linea = comboLineas.getValue();
        LocalDate fecha = datePickerFecha.getValue();

        List<RegistroBanio> registros =
                registroRepo.findByEmpleadoLineaNombreAndFecha(linea, fecha);

        String filtro = txtBuscarEmpleado.getText();

        if(filtro!=null && !filtro.isEmpty()){
            registros = registros.stream()
                    .filter(r -> r.getEmpleado().getNombre()
                            .toLowerCase()
                            .contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }

        tablaRegistros.setItems(
                FXCollections.observableArrayList(registros));
    }

    // ===========================
    // CONFIGURAR TABLA
    // ===========================
    private void configurarTabla(){

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "h:mm a / dd MMM yyyy",
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

        // ⭐ EVITAR COLUMNA VACÍA
        tablaRegistros.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS
        );
    }

    // ===========================
    // EXPORTAR PDF
    // ===========================
    @FXML
    public void exportarPDF(){

        List<RegistroBanio> visibles =
                tablaRegistros.getItems();

        if(visibles.isEmpty()){
            mostrarAlerta("No hay registros para exportar");
            return;
        }

        try{

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte PDF");

            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("PDF","*.pdf"));

            File archivo =
                    fileChooser.showSaveDialog(
                            (Stage) tablaRegistros.getScene().getWindow());

            if(archivo!=null){

                pdfService.generarReporte(
                        visibles,
                        archivo,
                        comboLineas.getValue(),
                        datePickerFecha.getValue());

                mostrarAlerta("PDF generado correctamente");
            }

        }catch(Exception e){
            e.printStackTrace();
            mostrarAlerta("Error generando PDF");
        }
    }

    // ===========================
    // SALIR SISTEMA
    // ===========================
    @FXML
    public void salirSistema(){
        JavaFxApplication.cambiarVista(
                "/templates/login.fxml",
                false
        );
    }

    // ===========================
    // ALERTAS
    // ===========================
    private void mostrarAlerta(String mensaje){

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}