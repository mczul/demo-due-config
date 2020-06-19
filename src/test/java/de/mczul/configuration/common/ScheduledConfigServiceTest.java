package de.mczul.configuration.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ScheduledConfigServiceTest {

    @Autowired
    private ScheduledConfigRepository entryRepository;
    @Autowired
    private ScheduledConfigService underTest;

    @Test
    void notExisting() {
        Optional<ScheduledConfigEntry> entry = underTest.get("MY_KEY_NOT_EXISTING");
        assertThat(entry).isEmpty();
    }

    @Test
    void onlyFuture() {
        final String KEY = "MY_KEY_ONLY_FUTURE";

        ScheduledConfigEntry first = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().plusMinutes(5))
                .value("1")
                .build();

        underTest.set(first);

        Optional<ScheduledConfigEntry> entry = underTest.get(KEY);
        assertThat(entry).isEmpty();
    }

    @Test
    void previousAndCurrentAndFuture() throws InterruptedException {
        final String KEY = "MY_KEY_PREVIOUS_AND_CURRENT_AND_FUTURE";

        ScheduledConfigEntry previous = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().minusMinutes(5))
                .value("1")
                .build();

        ScheduledConfigEntry current = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().minusSeconds(1))
                .value("2")
                .build();

        ScheduledConfigEntry future = ScheduledConfigEntry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().plusSeconds(1))
                .value("3")
                .build();

        underTest.set(previous);
        underTest.set(current);
        underTest.set(future);

        // Debug output
        entryRepository.findAll().stream()
                .map(ScheduledConfigEntry::toString)
                .forEach(log::info);

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

}



