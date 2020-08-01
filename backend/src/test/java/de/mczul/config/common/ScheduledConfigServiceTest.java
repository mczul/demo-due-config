package de.mczul.config.common;

import de.mczul.config.TestTags;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduledConfigService tests")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Slf4j
@Tag(TestTags.INTEGRATION_TEST)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ScheduledConfigServiceTest {

    @Autowired
    private ScheduledConfigRepository entryRepository;
    @Autowired
    private ScheduledConfigService underTest;

    @Test
    void not_existing() {
        Optional<ScheduledConfigEntry> entry = underTest.get("MY_KEY_NOT_EXISTING");
        assertThat(entry).isEmpty();
    }

    @Test
    void only_future() {
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
    void previous_and_current_and_future() throws InterruptedException {
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
                .forEach(LOG::info);

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



