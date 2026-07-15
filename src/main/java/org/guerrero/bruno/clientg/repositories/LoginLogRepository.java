package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
}
