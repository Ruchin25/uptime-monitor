package com.site.uptime.repository;

import com.site.uptime.domain.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckResultRepository extends JpaRepository<CheckResult, Long> {

    /** Most recent result for a monitor, used to detect up/down state changes and show current status. */
    Optional<CheckResult> findFirstByMonitorIdOrderByCheckedAtDesc(Long monitorId);
}
