package com.controlbano.control_salidas.controller;

import com.controlbano.control_salidas.entity.RegistroBanio;
import com.controlbano.control_salidas.service.RegistroBanioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


    @RestController
    @RequestMapping("/registro")
    public class RegistroController {

        @Autowired
        private RegistroBanioService service;

        @PostMapping("/scan")
        public RegistroBanio escanear(@RequestParam String carnet){
            return service.procesarEscaneo(carnet);
        }
    }

