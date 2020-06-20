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
        return entryRepository.findCurrentByKey(key);
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
