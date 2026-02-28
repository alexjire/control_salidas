package com.controlbano.control_salidas.service;

import com.controlbano.control_salidas.entity.Usuario;
import com.controlbano.control_salidas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario login(String username,String password){

        Usuario user = repo.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no existe"));

    /*
    ⭐ Si el usuario tiene password en BD
    → entonces debe validar contraseña
    */
        if(user.getPassword() != null && !user.getPassword().isEmpty()){

            if(password == null || password.isEmpty()){
                throw new RuntimeException("Ingrese contraseña");
            }

            if(!passwordEncoder.matches(password,user.getPassword())){
                throw new RuntimeException("Credenciales inválidas");
            }
        }

    /*
    ⭐ Si NO tiene password
    → entra directo (solo selección usuario)
    */
        return user;
    }
}