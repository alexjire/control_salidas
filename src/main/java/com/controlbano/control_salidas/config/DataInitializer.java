package com.controlbano.control_salidas.config;

import com.controlbano.control_salidas.entity.Usuario;
import com.controlbano.control_salidas.repository.UsuarioRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(
            UsuarioRepository repo,
            PasswordEncoder encoder){

        return args -> {

            if(repo.findByUsername("admin").isEmpty()){

                Usuario admin = new Usuario();

                admin.setUsername("admin");

                admin.setPassword(
                        encoder.encode("123456")
                );

                admin.setRol("ADMIN");
                admin.setActivo(true);

                repo.save(admin);
            }
        };
    }
}