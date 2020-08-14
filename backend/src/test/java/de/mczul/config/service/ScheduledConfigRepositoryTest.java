package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.testing.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduledConfigRepository tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
class ScheduledConfigRepositoryTest {

    @Autowired
    private ScheduledConfigRepository underTest;
    @Autowired
    private ScheduledConfigRepository repository;

    @BeforeEach
    void beforeEach() {
        repository.deleteAll();
    }

    @Transactional
    @Test
    void find_outdated() {
        var entries = List.of(
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(LocalDateTime.now().minusDays(1))
                        .value("1")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("y")
                        .validFrom(LocalDateTime.now().minusHours(12))
                        .value("2")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(LocalDateTime.now().minusHours(10))
                        .value("3")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(LocalDateTime.now().minusMinutes(30))
                        .value("4")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("y")
                        .validFrom(LocalDateTime.now().minusMinutes(15))
                        .value("5")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(LocalDateTime.now().plusDays(1))
                        .value("6")
                        .build()
        );

        underTest.saveAll(entries);

        var expectedEntries = entries.stream()
                .filter(e -> LocalDateTime.now().isAfter(e.getValidFrom()))
                .filter(e1 -> entries.stream().anyMatch(e2 -> Objects.equals(e2.getKey(), e1.getKey())
                        && e2.getValidFrom().isAfter(e1.getValidFrom())
                        && LocalDateTime.now().isAfter(e2.getValidFrom())))
                .collect(Collectors.toUnmodifiableList());
        var actualEntries = underTest.findOutdated();

        assertThat(actualEntries).hasSize(expectedEntries.size());
        assertThat(actualEntries).containsExactlyInAnyOrder(expectedEntries.toArray(ScheduledConfigEntry[]::new));
    }

    @Transactional
    @Test
    void find_current_by_key() {
        final String KEY = "MY_KEY";
        var entries = List.of(
                ScheduledConfigEntry.builder()
                        .key(KEY)
                        .value("1")
                        .validFrom(LocalDateTime.now().minusMinutes(3))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(KEY)
                        .value("2")
                        .validFrom(LocalDateTime.now().minusSeconds(2))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(KEY + "_OTHER")
                        .value("3")
                        .validFrom(LocalDateTime.now().minusSeconds(1))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(KEY)
                        .value("4")
                        .validFrom(LocalDateTime.now().plusSeconds(1))
                        .build()
        );

        underTest.saveAll(entries);

        ScheduledConfigEntry expectedValue = entries.stream()
                .filter(e -> Objects.equals(e.getKey(), KEY))
                .filter(e -> LocalDateTime.now().isAfter(e.getValidFrom()))
                .max(Comparator.comparing(ScheduledConfigEntry::getValidFrom))
                .orElseThrow();
        Optional<ScheduledConfigEntry> actualResult = underTest.findCurrentByKey(KEY);
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(expectedValue);
    }

}
