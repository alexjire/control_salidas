package com.controlbano.control_salidas.scheduler;

import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.repository.RegistroBanioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AutoCierreScheduler {

    @Autowired
    private RegistroBanioRepository repo;

    // Ejecutar cada 1 minuto
    @Scheduled(fixedRate = 60000)
    public void verificarRegistros(){

        List<RegistroBanio> abiertos =
                repo.findAll().stream()
                        .filter(r -> r.getEstado().equals("ABIERTO"))
                        .toList();

        LocalDateTime ahora = LocalDateTime.now();

        for(RegistroBanio registro : abiertos){

            if(registro.getHoraSalida()!=null){

                long minutos =
                        Duration.between(
                                registro.getHoraSalida(),
                                ahora
                        ).toMinutes();

                // Si pasa 20 minutos → cerrar automáticamente
                if(minutos >= 20){

                    registro.setHoraEntrada(ahora);
                    registro.setEstado("CERRADO");

                    repo.save(registro);
                }
            }
        }
    }
}