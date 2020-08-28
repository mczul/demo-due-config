package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.model.ScheduledConfigPast;
import de.mczul.config.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("ScheduledConfigMapper integration tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
class ScheduledConfigMapperIT {

    @Autowired
    private ScheduledConfigMapper underTest;
    @Autowired
    private ScheduledConfigRepository scheduledConfigRepository;

    @Transactional
    @Test
    void handle_history_properly() {
        final String key = "MY_ORDINARY_KEY";
        final var entryList = List.of(
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(1))
                        .value("1")
                        .created(ZonedDateTime.now().minusHours(12))
                        .author("A")
                        .comment("First Comment")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(6))
                        .value("2")
                        .author("B")
                        .created(ZonedDateTime.now().minusHours(6))
                        .comment("Second Comment")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key + "_X")
                        .validFrom(ZonedDateTime.now().plusHours(6))
                        .value("2")
                        .author("C")
                        .created(ZonedDateTime.now().minusHours(6))
                        .comment("A totally irrelevant comment")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(12))
                        .value("3")
                        .author("D")
                        .created(ZonedDateTime.now().minusHours(1))
                        .comment("Third Comment")
                        .build()
        );
        var relevantEntryList = entryList.stream()
                .filter(e -> key.equalsIgnoreCase(e.getKey()))
                .sorted(Comparator.comparing(ScheduledConfigEntry::getCreated).reversed())
                .collect(Collectors.toUnmodifiableList());

        // TODO: Check if mocking would be a faster alternative
        scheduledConfigRepository.saveAll(entryList);

        final var expected = relevantEntryList.stream()
                .map(ScheduledConfigEntry::getId)
                .collect(Collectors.toUnmodifiableList());
        final var latestEntry = relevantEntryList.stream()
                .findFirst().orElseThrow();
        var latestDto = underTest.fromDomain(latestEntry);
        var actual = latestDto.getHistory().stream()
                .map(ScheduledConfigPast::getId)
                .collect(Collectors.toUnmodifiableList());

        assertThat(actual).as("There are more or less comments than expected").hasSameSizeAs(expected);
        assertThat(actual).as("Actual comments differ from expected").containsExactlyElementsOf(expected);

        var i = 0;
        for (ScheduledConfigPast past : latestDto.getHistory()) {
            var entry = relevantEntryList.get(i++);
            assertThat(entry).as("A bug ...").isNotNull();
            assertThat(entry.getKey()).isEqualToIgnoringCase(key);
            assertAll(
                    () -> assertThat(past.getKey()).isEqualToIgnoringCase(key),
                    () -> assertThat(past.getAuthor()).isEqualTo(entry.getAuthor()),
                    () -> assertThat(past.getComment()).isEqualTo(entry.getComment()),
                    () -> assertThat(past.getCreated()).isEqualTo(entry.getCreated()),
                    () -> assertThat(past.getValidFrom()).isEqualTo(entry.getValidFrom()),
                    () -> assertThat(past.getValue()).isEqualTo(entry.getValue())
            );
        }
    }
}
