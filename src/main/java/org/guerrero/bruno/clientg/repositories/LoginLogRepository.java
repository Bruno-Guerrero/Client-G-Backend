package org.guerrero.bruno.clientg.repositories;

import org.guerrero.bruno.clientg.entities.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {

    List<LoginLog> findAllByUser_Id(Long userId);
}
