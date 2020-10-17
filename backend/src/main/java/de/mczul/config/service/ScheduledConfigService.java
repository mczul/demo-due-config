package de.mczul.config.service;

import de.mczul.config.AppConstants;
import de.mczul.config.model.ScheduledConfigEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@Service
@RequiredArgsConstructor
public class ScheduledConfigService {
    private final ScheduledConfigRepository entryRepository;

    @Transactional
    public ScheduledConfigEntry set(ScheduledConfigEntry entry) {
        return entryRepository.save(
                entry
                        // Assure uniform key representation
                        .withKey(entry.getKey().toLowerCase(AppConstants.DEFAULT_LOCALE))
                        // Set created timestamp
                        .withCreated(ZonedDateTime.now(ZoneId.of(AppConstants.DEFAULT_TIMEZONE)))
        );
    }

    @Transactional(readOnly = true)
    public Optional<ScheduledConfigEntry> get(String key) {
        return entryRepository.findCurrentByKey(key.toLowerCase(AppConstants.DEFAULT_LOCALE));
    }

    @Transactional
    @Scheduled(cron = "${de.mczul.config.cleanup.cron}")
    void cleanup() {
        List<ScheduledConfigEntry> obsolete = entryRepository.findOutdated();
        LOG.info("Found {} entries that are obsolete", obsolete.size());
    }
}
