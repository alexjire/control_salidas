package com.controlbano.control_salidas.repository;

import com.controlbano.control_salidas.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<Usuario,String> {

    Optional<Usuario> findByUsername(String username);

}