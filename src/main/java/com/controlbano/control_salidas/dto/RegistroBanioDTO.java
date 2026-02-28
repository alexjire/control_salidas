package com.controlbano.control_salidas.dto;

import java.time.LocalDateTime;

public class RegistroBanioDTO {

    private String carnet;
    private String nombre;
    private LocalDateTime horaSalida;
    private LocalDateTime horaEntrada;
    private Long duracionMinutos;
    private String estado;

    public RegistroBanioDTO(String carnet, String nombre,
                            LocalDateTime horaSalida,
                            LocalDateTime horaEntrada,
                            Long duracionMinutos,
                            String estado) {

        this.carnet = carnet;
        this.nombre = nombre;
        this.horaSalida = horaSalida;
        this.horaEntrada = horaEntrada;
        this.duracionMinutos = duracionMinutos;
        this.estado = estado;
    }

    // GETTERS

    public String getCarnet() { return carnet; }
    public String getNombre() { return nombre; }
    public LocalDateTime getHoraSalida() { return horaSalida; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public Long getDuracionMinutos() { return duracionMinutos; }
    public String getEstado() { return estado; }
}