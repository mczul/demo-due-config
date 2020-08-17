package de.mczul.config.validation;

import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("@NullOrNotBlank tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class NullOrNotBlankTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Data
    @Builder
    static class TestHostClass {
        public static final String MESSAGE_TEMPLATE = "{NullOrNotBlank.message}";
        @NullOrNotBlank(message = MESSAGE_TEMPLATE)
        private String value;
    }

    private final TestHostClass testHostInstance = TestHostClass.builder().build();

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
            "a",
            "1",
            ".",
            "#",
            "   a",
            "   1",
            "   .",
            "   #",
            "   <>"
    })
    void do_not_generate_constraint_violations_on_valid(String value) {
        testHostInstance.setValue(value);
        final var violations = VALIDATOR.validate(testHostInstance);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            " ",
            "   "
    })
    void generate_constraint_violations_on_invalid(String value) {
        testHostInstance.setValue(value);
        final var violations = VALIDATOR.validate(testHostInstance);
        assertThat(violations).as("There should only be one constraint violation.").hasSize(1);
        final var violation = violations.iterator().next();
        assertAll(
                () -> assertThat(violation.getPropertyPath().toString()).as("Property path not valid.").isEqualTo("value"),
                () -> assertThat(violation.getMessageTemplate()).as("Constraint violation message template not valid.").isEqualTo(TestHostClass.MESSAGE_TEMPLATE),
                () -> assertThat(violation.getRootBean()).isEqualTo(testHostInstance)
        );
    }

}
