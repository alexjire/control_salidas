package com.controlbano.control_salidas.controllerfx.dashboard;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.scene.control.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.controlbano.control_salidas.repository.RegistroBanioRepository;
import com.controlbano.control_salidas.repository.LineaRepository;
import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.service.PdfService;
import com.controlbano.control_salidas.JavaFxApplication;

import java.time.LocalDate;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DashboardController {

    @FXML
    private ComboBox<String> comboLineas;

    @FXML
    private DatePicker datePickerFecha;

    @FXML
    private TextField txtBuscarEmpleado;

    @FXML
    private TableView<RegistroBanio> tablaRegistros;

    @FXML
    private TableColumn<RegistroBanio, String> colCarnet;

    @FXML
    private TableColumn<RegistroBanio, String> colNombre;

    @FXML
    private TableColumn<RegistroBanio, String> colSalida;

    @FXML
    private TableColumn<RegistroBanio, String> colEntrada;

    @FXML
    private TableColumn<RegistroBanio, String> colDuracion;

    @FXML
    private TableColumn<RegistroBanio, String> colEstado;

    @Autowired
    private RegistroBanioRepository registroRepo;

    @Autowired
    private PdfService pdfService;

    @Autowired
    private LineaRepository lineaRepo;

    @FXML
    public void initialize() {

        datePickerFecha.setValue(LocalDate.now());

        cargarLineas();
        configurarTabla();

        comboLineas.setOnAction(e -> cargarTabla());
        datePickerFecha.setOnAction(e -> cargarTabla());

        txtBuscarEmpleado.textProperty().addListener((obs, oldVal, newVal) -> cargarTabla());
    }

    private void cargarLineas() {

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

    @FXML
    public void cargarTabla() {

        if (comboLineas.getValue() == null)
            return;

        String linea = comboLineas.getValue();
        LocalDate fecha = datePickerFecha.getValue();

        List<RegistroBanio> registros =
                registroRepo.findByEmpleadoLineaNombreAndFecha(linea, fecha);

        String filtro = txtBuscarEmpleado.getText();

        if (filtro != null && !filtro.isEmpty()) {

            registros = registros.stream()
                    .filter(r -> r.getEmpleado().getNombre()
                            .toLowerCase()
                            .contains(filtro.toLowerCase()))
                    .collect(Collectors.toList());
        }

        tablaRegistros.setItems(
                FXCollections.observableArrayList(registros)
        );
    }

    private void configurarTabla() {

        colCarnet.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmpleado().getCarnet()
                ));

        colNombre.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEmpleado().getNombre()
                ));

        colSalida.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getHoraSalida() != null
                                ? d.getValue().getHoraSalida().toString()
                                : ""
                ));

        colEntrada.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getHoraEntrada() != null
                                ? d.getValue().getHoraEntrada().toString()
                                : ""
                ));

        colDuracion.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getDuracionMinutos() != null
                                ? d.getValue().getDuracionMinutos() + " min"
                                : ""
                ));

        colEstado.setCellValueFactory(d ->
                new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getEstado()
                ));
    }

    @FXML
    public void exportarPDF() {

        List<RegistroBanio> visibles = tablaRegistros.getItems();

        if (visibles.isEmpty()) {
            mostrarAlerta("No hay registros para exportar");
            return;
        }

        try {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Reporte PDF");

            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));

            File archivo = fileChooser.showSaveDialog(
                    (Stage) tablaRegistros.getScene().getWindow()
            );

            if (archivo != null) {

                pdfService.generarReporte(
                        visibles,
                        archivo,
                        comboLineas.getValue(),
                        datePickerFecha.getValue()
                );

                mostrarAlerta("PDF generado correctamente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error generando PDF");
        }
    }

    private void mostrarAlerta(String mensaje) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    public void salirSistema() {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/templates/login.fxml")
            );

            loader.setControllerFactory(JavaFxApplication.context::getBean);

            Parent root = loader.load();

            Stage stage = (Stage) tablaRegistros.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}