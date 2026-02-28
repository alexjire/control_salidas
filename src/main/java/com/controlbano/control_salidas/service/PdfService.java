package com.controlbano.control_salidas.service;

import com.controlbano.control_salidas.entity.RegistroBanio;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.*;
import com.itextpdf.layout.element.*;
import com.itextpdf.io.image.ImageDataFactory;

import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    public void generarReporte(
            List<RegistroBanio> registros,
            File archivoDestino,
            String linea,
            LocalDate fecha) throws Exception {

        PdfWriter writer = new PdfWriter(archivoDestino);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // =========================
        // LOGO
        // =========================
        Image logo = new Image(
                ImageDataFactory.create("src/main/resources/static/img/Logo-Camtronics.png")
        );
        logo.scaleToFit(120, 80);
        document.add(logo);

        // =========================
        // TÍTULO CENTRADO (COMPATIBLE)
        // =========================
        Table tituloTable = new Table(1);
        tituloTable.setWidth(500); // ancho fijo compatible

        Cell tituloCell = new Cell();
        Paragraph titulo = new Paragraph("CONTROL DE SALIDAS AL BAÑO");
        titulo.setBold();
        titulo.setFontSize(16);

        tituloCell.add(titulo);
        tituloCell.setBorder(null);

        tituloTable.addCell(tituloCell);
        document.add(tituloTable);

        document.add(new Paragraph(" "));

        // =========================
        // INFORMACIÓN GENERAL
        // =========================
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        document.add(new Paragraph("Línea: " + linea));
        document.add(new Paragraph("Fecha: " + fecha.format(formatter)));
        document.add(new Paragraph("Total registros: " + registros.size()));
        document.add(new Paragraph(" "));

        // =========================
        // TABLA PRINCIPAL
        // =========================
        float[] widths = {90, 140, 90, 90, 80};
        Table table = new Table(widths);

        table.addCell("Carnet N°");
        table.addCell("Empleado");
        table.addCell("Hora Salida");
        table.addCell("Hora Entrada");
        table.addCell("Duración (min)");

        for (RegistroBanio r : registros) {

            String carnet = (r.getEmpleado() != null && r.getEmpleado().getCarnet() != null)
                    ? r.getEmpleado().getCarnet()
                    : "-";

            String nombre = (r.getEmpleado() != null)
                    ? r.getEmpleado().getNombre()
                    : "-";

            String salida = (r.getHoraSalida() != null)
                    ? r.getHoraSalida().toString()
                    : "-";

            String entrada = (r.getHoraEntrada() != null)
                    ? r.getHoraEntrada().toString()
                    : "-";

            String duracion = (r.getDuracionMinutos() != null)
                    ? r.getDuracionMinutos().toString()
                    : "-";

            table.addCell(carnet);
            table.addCell(nombre);
            table.addCell(salida);
            table.addCell(entrada);
            table.addCell(duracion);
        }

        document.add(table);
        document.close();
    }
}
//no cambiar