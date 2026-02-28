package com.controlbano.control_salidas.service;

import com.controlbano.control_salidas.entity.Empleado;
import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.repository.EmpleadoRepository;
import com.controlbano.control_salidas.repository.RegistroBanioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
public class RegistroBanioService {

    private static final int LOCK_SEGUNDOS = 5;

    @Autowired
    private RegistroBanioRepository registroRepo;

    @Autowired
    private EmpleadoRepository empleadoRepo;

    public RegistroBanio procesarEscaneo(String carnet){

        Empleado empleado = empleadoRepo.findByCarnet(carnet)
                .orElseThrow(() ->
                        new RuntimeException("Empleado no encontrado"));

        Optional<RegistroBanio> registroAbierto =
                registroRepo.findByEmpleadoCarnetAndEstado(
                        carnet,
                        "ABIERTO"
                );

        LocalDateTime ahora = LocalDateTime.now();

        /*
        ⭐ Anti doble escaneo
        */
        if(registroAbierto.isPresent()){

            RegistroBanio registro = registroAbierto.get();

            if(registro.getHoraSalida()!=null &&
                    Duration.between(
                            registro.getHoraSalida(),
                            ahora
                    ).getSeconds() < LOCK_SEGUNDOS){

                return registro;
            }
        }

        RegistroBanio registro;

        if(registroAbierto.isPresent()){

            registro = registroAbierto.get();

            registro.setHoraEntrada(ahora);

            if(registro.getHoraSalida()!=null){
                registro.setDuracionMinutos(
                        Duration.between(
                                registro.getHoraSalida(),
                                ahora
                        ).toMinutes()
                );
            }

            registro.setEstado("CERRADO");

        }else{

            registro = new RegistroBanio();
            registro.setEmpleado(empleado);
            registro.setFecha(LocalDate.now());
            registro.setHoraSalida(ahora);
            registro.setEstado("ABIERTO");
        }

        return registroRepo.save(registro);
    }

    public List<RegistroBanio> obtenerRegistrosDelDia(){
        return registroRepo.findByFecha(LocalDate.now());
    }
}