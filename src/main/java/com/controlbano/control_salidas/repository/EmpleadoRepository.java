package com.controlbano.control_salidas.repository;

import com.controlbano.control_salidas.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<Empleado, String> {

    Optional<Empleado> findByCarnet(String carnet);

}