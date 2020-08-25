package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("ScheduledConfigService tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
class ScheduledConfigServiceIT {

    @Autowired
    private ScheduledConfigRepository entryRepository;
    @Autowired
    private ScheduledConfigService underTest;

    @Test
    void return_empty_if_key_not_existing() {
        Optional<ScheduledConfigEntry> entry = underTest.get("MY_KEY_NOT_EXISTING");
        assertThat(entry).isEmpty();
    }

    @Test
    void return_empty_if_valid_from_in_future() {
        final String KEY = "MY_KEY_ONLY_FUTURE";

        ScheduledConfigEntry first = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(ZonedDateTime.now().plusMinutes(5))
                .value("1")
                .created(ZonedDateTime.now())
                .comment(null)
                .build();

        underTest.set(first);

        Optional<ScheduledConfigEntry> entry = underTest.get(KEY);
        assertThat(entry).isEmpty();
    }

    @Test
    void handle_valid_from_with_different_time_zones_correctly() {
        final String key = "MY_SAMPLE_KEY";
        var first = ScheduledConfigEntry.builder()
                .key(key)
                .validFrom(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Australia/Sydney")))
                .value("1")
                .build();
        var second = ScheduledConfigEntry.builder()
                .key(key)
                .validFrom(ZonedDateTime.of(LocalDateTime.now().minusMinutes(1), ZoneId.of("Europe/Berlin")))
                .value("2")
                .build();
        var third = ScheduledConfigEntry.builder()
                .key(key)
                .validFrom(ZonedDateTime.of(LocalDateTime.now().minusHours(1), ZoneId.of("America/Los_Angeles")))
                .value("3")
                .build();

        underTest.set(first);
        underTest.set(second);
        underTest.set(third);

        entryRepository.findAll();

        var result = underTest.get(key);

        assertThat(result.isPresent()).isTrue();
        var actual = result.get();
        assertThat(actual.getValue()).isEqualTo("2");
        assertThat(actual.getValidFrom()).isAfter(first.getValidFrom());
        assertThat(actual.getValidFrom()).isEqualTo(second.getValidFrom());
        assertThat(actual.getValidFrom()).isBefore(third.getValidFrom());
    }

    @Test
    void handle_valid_from_with_previous_and_current_and_future() throws InterruptedException {
        final String KEY = "MY_KEY_PREVIOUS_AND_CURRENT_AND_FUTURE";

        ScheduledConfigEntry previous = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(ZonedDateTime.now().minusMinutes(5))
                .value("1")
                .created(ZonedDateTime.now().minusHours(1))
                .build();

        ScheduledConfigEntry current = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(ZonedDateTime.now().minusSeconds(1))
                .value("2")
                .created(ZonedDateTime.now().minusMinutes(1))
                .build();

        ScheduledConfigEntry future = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(ZonedDateTime.now().plusSeconds(1))
                .value("3")
                .created(ZonedDateTime.now().minusSeconds(1))
                .build();

        underTest.set(previous);
        underTest.set(current);
        underTest.set(future);

        // Current
        Optional<ScheduledConfigEntry> entry = underTest.get(KEY);
        assertThat(entry).isPresent();
        assertThat(entry.get().getValue()).isEqualTo("2");

        // Wait for future
        Thread.sleep(1250);

        // Future
        entry = underTest.get(KEY);
        assertThat(entry).isPresent();
        assertThat(entry.get().getValue()).isEqualTo("3");
    }

    @Test
    void handle_unique_constraint_violations_properly() {
        final String KEY = "MY_KEY_UNIQUE_CONSTRAINT_VIOLATION";

        final ScheduledConfigEntry sample = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(ZonedDateTime.now().minusMinutes(5))
                .value("1")
                .created(ZonedDateTime.now().minusMinutes(1))
                .build();
        final ScheduledConfigEntry redundant = ScheduledConfigEntry.builder()
                .key(sample.getKey())
                .validFrom(sample.getValidFrom())
                .value(sample.getValue())
                .created(sample.getCreated())
                .build();

        underTest.set(sample);
        assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(
                () -> underTest.set(redundant)
        );
    }

}



