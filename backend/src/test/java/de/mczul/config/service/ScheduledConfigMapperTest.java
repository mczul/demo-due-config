package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.model.ScheduledConfigPast;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@DisplayName("ScheduledConfigMapper tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ScheduledConfigMapperTest {

    @Mock
    private ScheduledConfigRepository scheduledConfigRepository;
    @InjectMocks
    private ScheduledConfigMapperImpl underTest;

    @Test
    void handle_history_properly() {
        final String key = "MY_ORDINARY_KEY";
        final Random random = new Random();
        final var entryList = List.of(
                ScheduledConfigEntry.builder()
                        .id(random.nextInt())
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(1))
                        .value("1")
                        .created(ZonedDateTime.now().minusHours(12))
                        .author("A")
                        .comment("First Comment")
                        .build(),
                ScheduledConfigEntry.builder()
                        .id(random.nextInt())
                        .key(key)
                        .validFrom(ZonedDateTime.now().plusHours(6))
                        .value("2")
                        .author("B")
                        .created(ZonedDateTime.now().minusHours(6))
                        .comment("Second Comment")
                        .build(),
                ScheduledConfigEntry.builder()
                        .id(random.nextInt())
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

        for (var relevantEntry : relevantEntryList) {
            var olderEntries = relevantEntryList.stream()
                    .filter(current -> relevantEntry.getCreated().isAfter(current.getCreated()))
                    .collect(Collectors.toUnmodifiableList());

            when(scheduledConfigRepository.findHistory(relevantEntry.getKey(), relevantEntry.getCreated())).thenReturn(olderEntries);
            var dto = underTest.toDto(relevantEntry);
            verify(scheduledConfigRepository, times(1)).findHistory(relevantEntry.getKey(), relevantEntry.getCreated());
            assertThat(dto).isNotNull();

            var olderEntryIds = olderEntries.stream()
                    .map(ScheduledConfigEntry::getId)
                    .collect(Collectors.toUnmodifiableList());
            var dtoHistoryIds = dto.getHistory().stream()
                    .map(ScheduledConfigPast::getId)
                    .collect(Collectors.toUnmodifiableList());

            assertThat(dtoHistoryIds).as("There are more or less history entries than expected").hasSameSizeAs(olderEntryIds);
            assertThat(dtoHistoryIds).as("Actual comments differ from expected").containsExactlyElementsOf(olderEntryIds);
            assertThat(dtoHistoryIds).containsExactlyElementsOf(olderEntryIds);

            // Size of both list must be equal...
            for (var i = 0; i < olderEntries.size(); i++) {
                final var j = i;
                assertAll(
                        () -> assertThat(dto.getHistory().get(j).getKey()).isEqualToIgnoringCase(olderEntries.get(j).getKey()),
                        () -> assertThat(dto.getHistory().get(j).getAuthor()).isEqualTo(olderEntries.get(j).getAuthor()),
                        () -> assertThat(dto.getHistory().get(j).getComment()).isEqualTo(olderEntries.get(j).getComment()),
                        () -> assertThat(dto.getHistory().get(j).getCreated()).isEqualTo(olderEntries.get(j).getCreated()),
                        () -> assertThat(dto.getHistory().get(j).getValidFrom()).isEqualTo(olderEntries.get(j).getValidFrom()),
                        () -> assertThat(dto.getHistory().get(j).getValue()).isEqualTo(olderEntries.get(j).getValue())
                );
            }
        }
    }
}
