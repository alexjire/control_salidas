package com.controlbano.control_salidas.config;

import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.repository.RegistroBanioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class MonitorTiempoScheduler {

    @Autowired
    private RegistroBanioRepository repo;

    @Scheduled(fixedRate = 30000)
    public void verificarTiempoReal(){

        try{

            List<RegistroBanio> registros = repo.findAll();

            LocalDateTime ahora = LocalDateTime.now();

            for(RegistroBanio r : registros){

                if("ABIERTO".equals(r.getEstado()) &&
                        r.getHoraSalida()!=null){

                    long minutos =
                            Duration.between(
                                    r.getHoraSalida(),
                                    ahora
                            ).toMinutes();

                    if(minutos >= 20){

                        r.setEstado("AUTO_CERRADO");

                        if(r.getHoraEntrada()==null){
                            r.setHoraEntrada(ahora);
                        }

                        repo.save(r);
                    }
                }
            }

        }catch (Exception ignored){}
    }
}