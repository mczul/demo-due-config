package de.mczul.config.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

@DisplayName("ScheduledConfigEntry tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledConfigEntryTest extends AbstractScheduledConfigTest {

    @Override
    public Stream<ScheduledConfigEntry> buildValid() {
        return Stream.of(
                ScheduledConfigEntry.builder()
                        .key("MY_KEY_1")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_1")
                        .comment(null)
                        .created(ZonedDateTime.now().minusHours(1))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("MY_KEY_2")
                        .validFrom(ZonedDateTime.now())
                        .value(null)
                        .comment("New configuration entry for test purposes...")
                        .created(ZonedDateTime.now())
                        .build()
        );
    }

    @Override
    public Stream<ScheduledConfigEntry> buildInvalid() {
        return Stream.of(
                // Key is null
                ScheduledConfigEntry.builder()
                        .key(null)
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_1")
                        .created(ZonedDateTime.now())
                        .comment(null)
                        .build(),
                // Key is blank
                ScheduledConfigEntry.builder()
                        .key("")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_1")
                        .created(ZonedDateTime.now())
                        .comment(null)
                        .build(),
                // Key contains only whitespaces
                ScheduledConfigEntry.builder()
                        .key(" ")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_1")
                        .created(ZonedDateTime.now())
                        .comment(null)
                        .build(),
                // ValidFrom is null
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(null)
                        .value("MY_VALUE_2")
                        .created(ZonedDateTime.now())
                        .comment(null)
                        .build(),
                // Negative id
                ScheduledConfigEntry.builder()
                        .id(-1)
                        .key("MY_KEY")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_2")
                        .created(ZonedDateTime.now())
                        .comment(null)
                        .build(),
                // Blank comment
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_2")
                        .created(ZonedDateTime.now())
                        .comment(" ")
                        .build(),
                // Created in future
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(ZonedDateTime.now())
                        .value("MY_VALUE_3")
                        .created(ZonedDateTime.now().plusMinutes(1))
                        .comment(null)
                        .build()
        );
    }

    @Override
    protected ScheduledConfig createInstance() {
        return ScheduledConfigEntry.builder().build();
    }
}
