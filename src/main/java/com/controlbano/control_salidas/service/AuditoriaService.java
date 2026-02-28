package com.controlbano.control_salidas.service;

import com.controlbano.control_salidas.entity.AuditoriaLog;
import com.controlbano.control_salidas.repository.AuditoriaLogRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaService {

    @Autowired
    private AuditoriaLogRepository repo;

    public void registrar(String usuario,
                          String accion,
                          String detalle){

        AuditoriaLog log = new AuditoriaLog();

        log.setUsuario(usuario);
        log.setAccion(accion);
        log.setDetalle(detalle);
        log.setFechaHora(LocalDateTime.now());

        repo.save(log);
    }
}