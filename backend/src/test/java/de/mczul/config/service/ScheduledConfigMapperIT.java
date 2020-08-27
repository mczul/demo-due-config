package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.testing.IntegrationTest;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@DisplayName("ScheduledConfigMapper tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
class ScheduledConfigMapperIT {

    @Autowired
    private ScheduledConfigMapper underTest;
    @Autowired
    private ScheduledConfigRepository scheduledConfigRepository;

    @Transactional
    @Test
    void handle_comments_history_properly() {
        final String key = "MY_ORDINARY_KEY";
        var entryList = List.of(
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(1))
                        .value("1")
                        .comment("First Comment")
                        .created(ZonedDateTime.now().minusHours(12))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(6))
                        .value("2")
                        .comment("Second Comment")
                        .created(ZonedDateTime.now().minusHours(6))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(12))
                        .value("3")
                        .comment("Third Comment")
                        .created(ZonedDateTime.now().minusHours(1))
                        .build()
        );

        scheduledConfigRepository.saveAll(entryList);

        var expected = entryList.stream()
                .sorted(Comparator.comparing(ScheduledConfigEntry::getCreated).reversed())
                .map(ScheduledConfigEntry::getComment)
                .collect(Collectors.toUnmodifiableList());

        var actual = entryList.stream()
                .sorted(Comparator.comparing(ScheduledConfigEntry::getCreated).reversed())
                .map(underTest::fromDomain)
                .findFirst().orElseThrow()
                .getCommentHistory();

        assertThat(actual).as("There are more or less comments than expected").hasSameSizeAs(expected);
        assertThat(actual).as("Actual comments differ from expected").containsExactlyElementsOf(expected);
    }
}