package de.mczul.configuration.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduledConfigRepository extends JpaRepository<ScheduledConfigEntry, Integer> {

    List<ScheduledConfigEntry> findByKey(String key);

    @Query("select e1 " +
            "from ScheduledConfigEntry e1 " +
            "where e1.key = ?1 " +
            "and e1.validFrom <= current_timestamp " +
            "and not exists( " +
            "   select 'x' " +
            "   from ScheduledConfigEntry e2 " +
            "   where e1.key = e2.key " +
            "   and e2.validFrom <= current_timestamp " +
            "   and e2.validFrom > e1.validFrom " +
            ")")
    Optional<ScheduledConfigEntry> findCurrentByKey(String key);

    @Query("select e1 " +
            "from ScheduledConfigEntry e1 " +
            "where e1.validFrom <= current_timestamp " +
            "and exists( " +
            "   select 'x' " +
            "   from ScheduledConfigEntry e2 " +
            "   where e1.key = e2.key " +
            "   and e2.validFrom <= current_timestamp " +
            "   and e2.validFrom > e1.validFrom " +
            ")")
    List<ScheduledConfigEntry> findOutdated();
}
