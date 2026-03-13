package com.controlbano.control_salidas.repository;

import com.controlbano.control_salidas.entity.RegistroBanio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RegistroBanioRepository
        extends JpaRepository<RegistroBanio,Long>{

    /*
    ⭐ Buscar registro abierto del empleado
    */
    Optional<RegistroBanio> findByEmpleadoCarnetAndEstado(
            String carnet,
            String estado
    );

    /*
    ⭐ Buscar registros por línea y fecha
    Ruta:
    RegistroBanio
      ↓
    Empleado
      ↓
    Linea
      ↓
    nombre
    */
    List<RegistroBanio> findByEmpleadoLineaNombreAndFecha(
            String nombreLinea,
            LocalDate fecha
    );

    /*
    ⭐ Registros del día
    */
    List<RegistroBanio> findByFecha(
            LocalDate fecha
    );

}