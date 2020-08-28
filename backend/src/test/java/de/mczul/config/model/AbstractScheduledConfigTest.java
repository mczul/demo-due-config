package de.mczul.config.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Provides basic testing of common aspects.
 * <p>
 * Important: Inheritance requires two static methods to provide valid and invalid samples for parameterized tests
 *
 * @see ScheduledConfigEntryTest#buildValid()
 * @see ScheduledConfigEntryTest#buildInvalid()
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractScheduledConfigTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    protected abstract ScheduledConfig createInstance();

    public abstract Stream<? extends ScheduledConfig> buildValid();

    public abstract Stream<? extends ScheduledConfig> buildInvalid();

    @BeforeAll
    void beforeAll() {
        assertThat(buildValid().count()).as("Not enough valid samples provided").isGreaterThan(1);
        assertThat(buildInvalid().count()).as("Not enough invalid samples provided").isGreaterThan(1);
    }

    @ParameterizedTest
    @MethodSource("buildValid")
    void valid_samples_must_not_produce_violations(ScheduledConfig sample) {
        final Set<ConstraintViolation<ScheduledConfig>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("buildInvalid")
    void invalid_samples_must_produce_violations(ScheduledConfig sample) {
        final Pattern acceptableMessageTemplatesPattern = Pattern.compile("^\\{(?:ValidConfigKey|(?:Not(?:Blank|Null)|Positive|NullOrNotBlank|PastOrPresent)\\.scheduledConfig\\..+)\\.message}$");
        final Set<ConstraintViolation<ScheduledConfig>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isNotEmpty();
        for (ConstraintViolation<ScheduledConfig> violation : violations) {
            assertThat(violation.getMessageTemplate()).matches(acceptableMessageTemplatesPattern);
            assertThat(violation.getMessage()).doesNotMatch(acceptableMessageTemplatesPattern);
        }
    }

    @Test
    void equals_and_hash_code() {
        final var id = 42;
        final var key = "MY_KEY";
        final var validFrom = ZonedDateTime.now();
        final var value = "MY_VALUE";

        final var first = createInstance()
                .withId(null)
                .withKey(key)
                .withValidFrom(validFrom)
                .withValue(value);
        final var second = createInstance()
                .withId(null)
                .withKey(key)
                .withValidFrom(validFrom)
                .withValue(value);

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
