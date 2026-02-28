package com.controlbano.control_salidas.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity

public class Empleado {
    @Id
    private String carnet;

    private String nombre;

    @ManyToOne
    @JoinColumn(name="linea_id")
    private Linea linea;

    public String getCarnet() {
        return carnet;
    }

    public void setCarnet(String carnet) {
        this.carnet = carnet;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Linea getLinea() {
        return linea;
    }

    public void setLinea(Linea linea) {
        this.linea = linea;
    }
}
