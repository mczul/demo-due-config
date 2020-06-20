package de.mczul.configuration.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduledConfigRepository extends JpaRepository<ScheduledConfigEntry, Integer> {

    List<ScheduledConfigEntry> findByKey(String key);

    @Query("SELECT e1 " +
            "FROM ScheduledConfigEntry e1 " +
            "WHERE e1.key = ?1 " +
            "AND e1.validFrom <= current_timestamp " +
            "AND NOT EXISTS( " +
            "   SELECT 'x' " +
            "   FROM ScheduledConfigEntry e2 " +
            "   WHERE e1.key = e2.key " +
            "   AND e2.validFrom <= current_timestamp " +
            "   AND e2.validFrom > e1.validFrom " +
            ")")
    Optional<ScheduledConfigEntry> findCurrentByKey(String key);

    @Query("SELECT e1 " +
            "FROM ScheduledConfigEntry e1 " +
            "WHERE e1.validFrom <= current_timestamp " +
            "AND EXISTS( " +
            "   SELECT 'x' " +
            "   FROM ScheduledConfigEntry e2 " +
            "   WHERE e1.key = e2.key " +
            "   AND e2.validFrom <= current_timestamp " +
            "   AND e2.validFrom > e1.validFrom " +
            ")")
    List<ScheduledConfigEntry> findOutdated();
}
