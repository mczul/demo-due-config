package de.mczul.configuration.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class EntryServiceTest {

    @Autowired
    private EntryRepository entryRepository;
    @Autowired
    private EntryService underTest;

    @Test
    void notExisting() {
        Optional<Entry> entry = underTest.get("MY_KEY_NOT_EXISTING");
        assertThat(entry).isEmpty();
    }

    @Test
    void onlyFuture() {
        final String KEY = "MY_KEY_ONLY_FUTURE";

        Entry first = Entry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().plusMinutes(5))
                .value("1")
                .build();

        underTest.set(first);

        Optional<Entry> entry = underTest.get(KEY);
        assertThat(entry).isEmpty();
    }

    @Test
    void previousAndCurrentAndFuture() throws InterruptedException {
        final String KEY = "MY_KEY_PREVIOUS_AND_CURRENT_AND_FUTURE";

        Entry previous = Entry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().minusMinutes(5))
                .value("1")
                .build();

        Entry current = Entry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().minusSeconds(2))
                .value("2")
                .build();

        Entry future = Entry.builder()
                .key(KEY)
                .validFrom(LocalDateTime.now().plusSeconds(2))
                .value("3")
                .build();

        underTest.set(previous);
        underTest.set(current);
        underTest.set(future);

        // Debug output
        entryRepository.findAll().stream()
                .map(Entry::toString)
                .forEach(log::info);

        // Current
        Optional<Entry> entry = underTest.get(KEY);
        assertThat(entry).isPresent();
        assertThat(entry.get().getValue()).isEqualTo("2");

        Thread.sleep(4000, 0);

        // Future
        entry = underTest.get(KEY);
        assertThat(entry).isPresent();
        assertThat(entry.get().getValue()).isEqualTo("3");
    }

}



