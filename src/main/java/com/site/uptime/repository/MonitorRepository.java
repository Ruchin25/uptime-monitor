package com.site.uptime.repository;

import com.site.uptime.domain.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitorRepository extends JpaRepository<Monitor, Long> {
    List<Monitor> findByEnabledTrue();
}
