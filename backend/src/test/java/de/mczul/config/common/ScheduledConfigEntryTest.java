package de.mczul.config.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
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
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ScheduledConfigEntryTest {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static Stream<ScheduledConfigEntry> buildValid() {
        return Stream.of(
                ScheduledConfigEntry.builder()
                        .key("MY_KEY_1")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .build(),
                ScheduledConfigEntry.builder()
                        .key("MY_KEY_2")
                        .validFrom(LocalDateTime.now())
                        .value(null)
                        .build()
        );
    }

    static Stream<ScheduledConfigEntry> buildInvalid() {
        return Stream.of(
                // Key is null
                ScheduledConfigEntry.builder()
                        .key(null)
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .build(),
                // Key is blank
                ScheduledConfigEntry.builder()
                        .key("")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .build(),
                // Key contains only whitespaces
                ScheduledConfigEntry.builder()
                        .key(" ")
                        .validFrom(LocalDateTime.now())
                        .value("MY_VALUE_1")
                        .build(),
                // ValidFrom is null
                ScheduledConfigEntry.builder()
                        .key("MY_KEY")
                        .validFrom(null)
                        .value("MY_VALUE_2")
                        .build()
        );
    }

    @ParameterizedTest
    @MethodSource("buildValid")
    void valid_samples_must_not_produce_violations(ScheduledConfigEntry sample) {
        Set<ConstraintViolation<ScheduledConfigEntry>> violations = validator.validate(sample);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("buildInvalid")
    void invalid_samples_must_produce_violations(ScheduledConfigEntry sample) {
        final Pattern acceptableMessageTemplatesPattern = Pattern.compile("^\\{Not(?:Blank|Null)\\.scheduledConfig\\..+}$");
        Set<ConstraintViolation<ScheduledConfigEntry>> violations = validator.validate(sample);
        assertThat(violations).isNotEmpty();
        for (ConstraintViolation<ScheduledConfigEntry> violation : violations) {
            assertThat(violation.getMessageTemplate()).matches(acceptableMessageTemplatesPattern);
            assertThat(violation.getMessage()).doesNotMatch(acceptableMessageTemplatesPattern);
        }
    }

    @Test
    void equals_and_hash_code() {
        var referenceTimestamp = LocalDateTime.now();
        var first = ScheduledConfigEntry.builder()
                .id(null)
                .key("MY_KEY_1")
                .validFrom(referenceTimestamp)
                .value("MY_VALUE_1")
                .build();
        var second = ScheduledConfigEntry.builder()
                .id(null)
                .key("MY_KEY_1")
                .validFrom(referenceTimestamp)
                .value("MY_VALUE_1")
                .build();
        // Entities are only equal if their id is equal
        assertThat(first).isNotEqualTo(second);

        first.setId(42);
        second.setId(42);
        assertThat(first).isEqualTo(second);

        second.setKey(second.getKey() + "_NEW");
        assertThat(first).isEqualTo(second);
    }
}