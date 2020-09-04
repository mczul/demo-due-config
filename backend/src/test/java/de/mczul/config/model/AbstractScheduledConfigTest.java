package de.mczul.config.model;

import de.mczul.config.AppConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.*;
import java.time.ZonedDateTime;
import java.util.Locale;
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
    public static final Pattern MESSAGE_TEMPLATE_INDICATOR_PATTERN = Pattern.compile("^\\{");
    public static final Pattern ACCEPTABLE_MESSAGE_TEMPLATES_PATTERN = Pattern.compile("^\\{(?:ValidConfigKey|(?:Not(?:Blank|Null)|Positive|NullOrNotBlank|PastOrPresent)\\.scheduledConfig\\..+)\\.message}$");
    private static final Locale[] MANDATORY_VALIDATION_LOCALES = {Locale.ENGLISH, Locale.GERMAN};
    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    private static final MessageInterpolator VALIDATOR_MESSAGE_INTERPOLATOR = VALIDATOR_FACTORY.getMessageInterpolator();
    private static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

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
        final Set<ConstraintViolation<ScheduledConfig>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isNotEmpty();

        for (ConstraintViolation<ScheduledConfig> violation : violations) {
            String messageTemplate = violation.getMessageTemplate();
            assertThat(messageTemplate).matches(ACCEPTABLE_MESSAGE_TEMPLATES_PATTERN);
            assertThat(violation.getMessage())
                    .as("No message translation for '%s' in default locale", messageTemplate)
                    .doesNotMatch(MESSAGE_TEMPLATE_INDICATOR_PATTERN);

            // Fetch the constraint violation message for the default locale
//            String defaultMessage = VALIDATOR_MESSAGE_INTERPOLATOR.interpolate(messageTemplate, null);
            // Iterate over all additional locales that must be supported
            for (Locale mandatoryLocale : MANDATORY_VALIDATION_LOCALES) {
                final String message = VALIDATOR_MESSAGE_INTERPOLATOR.interpolate(messageTemplate, null, mandatoryLocale);
                // TODO: Check why this assertion fails
//                assertThat(message).as("No localized message provided for '%s'", mandatoryLocale.getDisplayName()).isNotEqualToIgnoringCase(defaultMessage);
                assertThat(message)
                        .as("No message translation for '%s' with locale '%s'", messageTemplate, mandatoryLocale.getDisplayName(AppConstants.DEFAULT_LOCALE))
                        .doesNotMatch(MESSAGE_TEMPLATE_INDICATOR_PATTERN);
            }
        }
    }

    @Test
    void equals_and_hash_code() {
        final var first = createInstance()
                .withId(null)
                .withKey("MY_KEY")
                .withValidFrom(ZonedDateTime.now())
                .withValue("MY_VALUE")
                .withCreated(ZonedDateTime.now())
                .withAuthor("ABC")
                .withComment("This is serious...");
        final var second = createInstance()
                .withId(null)
                .withKey(first.getKey())
                .withValidFrom(first.getValidFrom())
                .withValue(first.getValue())
                .withCreated(first.getCreated())
                .withAuthor(first.getAuthor())
                .withComment(first.getComment());

        // Entities are only equal if their id is equal
        assertThat(first).isNotEqualTo(second);

        first.setId(42);
        second.setId(null);
        assertThat(first).isNotEqualTo(second);

        // Set same id on both instances
        second.setId(first.getId());
        assertThat(first).isEqualTo(second);

        // Changing the key must not make any equality difference
        second.setKey(second.getKey() + "_NEW");
        assertThat(first).isEqualTo(second);
    }

}
