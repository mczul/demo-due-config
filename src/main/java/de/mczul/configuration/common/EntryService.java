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
public class EntryService {
    private final EntryRepository entryRepository;

    @Transactional
    public void set(Entry entry) {
        entryRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public Optional<Entry> get(String key) {
        return entryRepository.findByKey(key).stream()
                // Remove all future configuration values
                .filter(e -> !e.getValidFrom().isAfter(LocalDateTime.now()))
                // Sort configuration entries descending by "validFrom" timestamp
                .sorted(Comparator.comparing(Entry::getValidFrom).reversed())
                // Fetch the one with the latest "validFrom" value
                .findFirst();
    }

    @Transactional
    @Scheduled(fixedRate = 5000)
    void cleanup() {
        List<Entry> activeOrObsolete = entryRepository.findAll().stream()
                .filter(e -> e.getValidFrom().isBefore(LocalDateTime.now()))
                .collect(Collectors.toUnmodifiableList());
        log.info("Found {} entries that are active or obsolete", activeOrObsolete.size());
    }
}
