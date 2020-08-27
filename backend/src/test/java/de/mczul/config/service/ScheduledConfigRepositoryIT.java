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

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduledConfigRepository tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
class ScheduledConfigRepositoryIT {

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
    void load_comments_by_key() {
        final String key = "MY_CRYPTIC_KEY";
        var entries = List.of(
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(1))
                        .value("1")
                        .comment("Valid in 1 hour; Set 1 hour ago")
                        .created(ZonedDateTime.now().minusHours(1))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(12))
                        .value("2")
                        .comment("Valid in 12 hours; Set 12 hours ago")
                        .created(ZonedDateTime.now().minusHours(12))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key + "_X")
                        .validFrom(ZonedDateTime.now().plusHours(6))
                        .value("X")
                        .comment("Something completely irrelevant")
                        .created(ZonedDateTime.now().minusHours(6))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(24))
                        .value("3")
                        .comment("Valid in 24 hours; Set 24 hours ago")
                        .created(ZonedDateTime.now().minusHours(24))
                        .build()
        );

        underTest.saveAll(entries);

        // Descending order by creation timestamp
        var expectedComments = entries.stream()
                .filter(entry -> key.equalsIgnoreCase(entry.getKey()))
                .sorted(Comparator.comparing(ScheduledConfigEntry::getCreated).reversed())
                .map(ScheduledConfigEntry::getComment)
                .collect(Collectors.toUnmodifiableList());
        // Database query
        var actualComments = underTest.loadCommentsByKey(key);

        assertThat(actualComments).as("Number of comments differ").hasSameSizeAs(expectedComments);
        assertThat(actualComments).as("Actual comment list did not contain all expected entries and / or not in expected order")
                .containsExactly(expectedComments.toArray(String[]::new));
    }

    @Transactional
    @Test
    void find_outdated() {
        var entries = List.of(
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(ZonedDateTime.now().minusDays(1))
                        .value("1")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("y")
                        .validFrom(ZonedDateTime.now().minusHours(12))
                        .value("2")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(ZonedDateTime.now().minusHours(10))
                        .value("3")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(ZonedDateTime.now().minusMinutes(30))
                        .value("4")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("y")
                        .validFrom(ZonedDateTime.now().minusMinutes(15))
                        .value("5")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("x")
                        .validFrom(ZonedDateTime.now().plusDays(1))
                        .value("6")
                        .created(ZonedDateTime.now())
                        .build()
        );

        underTest.saveAll(entries);

        var expectedEntries = entries.stream()
                .filter(e -> ZonedDateTime.now().isAfter(e.getValidFrom()))
                .filter(e1 -> entries.stream().anyMatch(e2 -> Objects.equals(e2.getKey(), e1.getKey())
                        && e2.getValidFrom().isAfter(e1.getValidFrom())
                        && ZonedDateTime.now().isAfter(e2.getValidFrom())))
                .collect(Collectors.toUnmodifiableList());
        var actualEntries = underTest.findOutdated();

        assertThat(actualEntries).hasSameSizeAs(expectedEntries);
        assertThat(actualEntries).containsExactlyInAnyOrder(expectedEntries.toArray(ScheduledConfigEntry[]::new));
    }

    @Transactional
    @Test
    void find_current_by_key() {
        final String KEY = "MY_KEY";
        var entries = List.of(
                ScheduledConfigEntry.builder()
                        .key(KEY)
                        .validFrom(ZonedDateTime.now().minusMinutes(3))
                        .value("1")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(KEY)
                        .validFrom(ZonedDateTime.now().minusSeconds(2))
                        .value("2")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(KEY + "_OTHER")
                        .validFrom(ZonedDateTime.now().minusSeconds(1))
                        .value("3")
                        .created(ZonedDateTime.now())
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(KEY)
                        .validFrom(ZonedDateTime.now().plusSeconds(1))
                        .value("4")
                        .created(ZonedDateTime.now())
                        .build()
        );

        underTest.saveAll(entries);

        ScheduledConfigEntry expectedValue = entries.stream()
                .filter(e -> Objects.equals(e.getKey(), KEY))
                .filter(e -> ZonedDateTime.now().isAfter(e.getValidFrom()))
                .max(Comparator.comparing(ScheduledConfigEntry::getValidFrom))
                .orElseThrow();
        Optional<ScheduledConfigEntry> actualResult = underTest.findCurrentByKey(KEY);
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(expectedValue);
    }

}
