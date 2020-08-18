package de.mczul.config.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduledConfigEntry tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledConfigEntryTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    public static Stream<ScheduledConfigEntry> buildValid() {
        return Stream.of(
                ScheduledConfigEntry.builder()
                        .key("MY_KEY_1")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .comment(null)
                        .created(LocalDateTime.now().minusHours(1))
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("MY_KEY_2")
                        .validFrom(LocalDateTime.now())
                        .value(null)
                        .comment("New configuration entry for test purposes...")
                        .created(LocalDateTime.now())
                        .build()
        );
    }

    public static Stream<ScheduledConfigEntry> buildInvalid() {
        return Stream.of(
                // Key is null
                ScheduledConfigEntry.builder()
                        .key(null)
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .created(LocalDateTime.now())
                        .comment(null)
                        .build(),
                // Key is blank
                ScheduledConfigEntry.builder()
                        .key("")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .created(LocalDateTime.now())
                        .comment(null)
                        .build(),
                // Key contains only whitespaces
                ScheduledConfigEntry.builder()
                        .key(" ")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .created(LocalDateTime.now())
                        .comment(null)
                        .build(),
                // ValidFrom is null
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(null)
                        .value("MY_VALUE_2")
                        .created(LocalDateTime.now())
                        .comment(null)
                        .build(),
                // Negative id
                ScheduledConfigEntry.builder()
                        .id(-1)
                        .key("MY_KEY")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_2")
                        .created(LocalDateTime.now())
                        .comment(null)
                        .build(),
                // Blank comment
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_2")
                        .created(LocalDateTime.now())
                        .comment(" ")
                        .build(),
                // Created in future
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_3")
                        .created(LocalDateTime.now().plusMinutes(1))
                        .comment(null)
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("buildValid")
    void valid_samples_must_not_produce_violations(ScheduledConfigEntry sample) {
        final Set<ConstraintViolation<ScheduledConfigEntry>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("buildInvalid")
    void invalid_samples_must_produce_violations(ScheduledConfigEntry sample) {
        final Pattern acceptableMessageTemplatesPattern = Pattern.compile("^\\{(?:ValidConfigKey|(?:Not(?:Blank|Null)|Positive|NullOrNotBlank|PastOrPresent)\\.scheduledConfig\\..+)\\.message}$");
        final Set<ConstraintViolation<ScheduledConfigEntry>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isNotEmpty();
        for (ConstraintViolation<ScheduledConfigEntry> violation : violations) {
            assertThat(violation.getMessageTemplate()).matches(acceptableMessageTemplatesPattern);
            assertThat(violation.getMessage()).doesNotMatch(acceptableMessageTemplatesPattern);
        }
    }

    @Test
    void equals_and_hash_code() {
        final var id = 42;
        final var key = "MY_KEY";
        final var validFrom = LocalDateTime.now();
        final var value = "MY_VALUE";

        final var first = ScheduledConfigEntry.builder()
                .id(null)
                .key(key)
                .validFrom(validFrom)
                .value(value)
                .build();
        final var second = ScheduledConfigEntry.builder()
                .id(null)
                .key(key)
                .validFrom(validFrom)
                .value(value)
                .build();

        // Entities are only equal if their id is equal
        assertThat(first).isNotEqualTo(second);

        first.setId(id);
        second.setId(null);
        assertThat(first).isNotEqualTo(second);

        // Set same id on both instances
        first.setId(id);
        second.setId(id);
        assertThat(first).isEqualTo(second);

        // Changing the key must not make any equality difference
        second.setKey(second.getKey() + "_NEW");
        assertThat(first).isEqualTo(second);
    }
}
