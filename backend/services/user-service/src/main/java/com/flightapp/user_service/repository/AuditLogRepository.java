package com.flightapp.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.flightapp.user_service.model.AuditLog;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long>{

}
