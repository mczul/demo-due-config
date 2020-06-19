package de.mczul.configuration.common;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ScheduledConfigRepositoryTest {

    @Autowired
    private ScheduledConfigRepository underTest;

    @Transactional
    @Test
    void dummy() {
        final String KEY = "MY_KEY";
        var first = ScheduledConfigEntry.builder()
                .key(KEY)
                .value("1")
                .validFrom(LocalDateTime.now().minusMinutes(5))
                .build();

        var second = ScheduledConfigEntry.builder()
                .key(KEY)
                .value("2")
                .validFrom(LocalDateTime.now().minusSeconds(1))
                .build();

        var third = ScheduledConfigEntry.builder()
                .key(KEY + "_OTHER")
                .value("3")
                .validFrom(LocalDateTime.now().minusSeconds(1))
                .build();

        var fourth = ScheduledConfigEntry.builder()
                .key(KEY)
                .value("4")
                .validFrom(LocalDateTime.now().plusSeconds(1))
                .build();

        underTest.save(first);
        underTest.save(second);
        underTest.save(third);
        underTest.save(fourth);

        List<ScheduledConfigEntry> all = underTest.findAll();

        assertThat(all).hasSize(4);

        Optional<ScheduledConfigEntry> actualResult = underTest.findCurrentByKey(KEY);
        assertThat(actualResult).isPresent();
        assertThat(actualResult.get().getValue()).isEqualTo(second.getValue());
    }

}