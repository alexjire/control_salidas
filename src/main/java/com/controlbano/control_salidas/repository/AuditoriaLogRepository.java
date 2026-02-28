package com.controlbano.control_salidas.repository;

import com.controlbano.control_salidas.entity.AuditoriaLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaLogRepository
        extends JpaRepository<AuditoriaLog,Long>{
}