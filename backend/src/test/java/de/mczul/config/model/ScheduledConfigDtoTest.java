package de.mczul.config.model;

import de.mczul.config.service.ScheduledConfigMapper;
import de.mczul.config.service.ScheduledConfigMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ScheduledConfigDto tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledConfigDtoTest {

    // TODO: Test validation without duplicating code
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
    private static final ScheduledConfigMapper MAPPER = new ScheduledConfigMapperImpl();

    static Stream<ScheduledConfigDto> buildValid() {
        return ScheduledConfigEntryTest.buildValid().map(MAPPER::fromDomain);
    }

    public static Stream<ScheduledConfigDto> buildInvalid() {
        return ScheduledConfigEntryTest.buildInvalid().map(MAPPER::fromDomain);
    }

    @ParameterizedTest
    @MethodSource("buildValid")
    void valid_samples_must_not_produce_violations(ScheduledConfigDto sample) {
        Set<ConstraintViolation<ScheduledConfigDto>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("buildInvalid")
    void invalid_samples_must_produce_violations(ScheduledConfigDto sample) {
        final Pattern acceptableMessageTemplatesPattern = Pattern.compile("^\\{(ValidConfigKey|Not(?:Blank|Null)\\.scheduledConfig\\..+)\\.message}$");
        final Set<ConstraintViolation<ScheduledConfigDto>> violations = VALIDATOR.validate(sample);
        assertThat(violations).isNotEmpty();
        for (ConstraintViolation<ScheduledConfigDto> violation : violations) {
            assertThat(violation.getMessageTemplate()).matches(acceptableMessageTemplatesPattern);
            assertThat(violation.getMessage()).doesNotMatch(acceptableMessageTemplatesPattern);
        }
    }

}