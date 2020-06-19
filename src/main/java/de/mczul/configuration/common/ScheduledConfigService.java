package de.mczul.configuration.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledConfigService {
    private final ScheduledConfigRepository entryRepository;

    @Transactional
    public void set(ScheduledConfigEntry entry) {
        entryRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public Optional<ScheduledConfigEntry> get(String key) {
        // Remove all future configuration values
        // Sort configuration entries descending by "validFrom" timestamp
        // Fetch the one with the latest "validFrom" value
        return entryRepository.findByKey(key).stream()
                // Remove all future configuration values
                .filter(e -> !e.getValidFrom().isAfter(LocalDateTime.now()))
                // Fetch the config entry with the max "valid from" value
                .max(Comparator.comparing(ScheduledConfigEntry::getValidFrom));
    }

    @Transactional
    @Scheduled(fixedRate = 5000)
    void cleanup() {
        List<ScheduledConfigEntry> activeOrObsolete = entryRepository.findAll().stream()
                .filter(e -> e.getValidFrom().isBefore(LocalDateTime.now()))
                .collect(Collectors.toUnmodifiableList());
        log.info("Found {} entries that are active or obsolete", activeOrObsolete.size());
    }
}
