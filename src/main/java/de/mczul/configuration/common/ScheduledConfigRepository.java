package de.mczul.configuration.common;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledConfigRepository extends JpaRepository<ScheduledConfigEntry, Integer> {

    List<ScheduledConfigEntry> findByKey(String key);

}
