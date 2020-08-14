package de.mczul.config.validation;

import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("@ValidConfigKey tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
public class ValidConfigKeyTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Data
    @Builder
    static class TestHostClass {
        @ValidConfigKey
        private String key;
    }

    private final TestHostClass testHostInstance = TestHostClass.builder().build();

    @ParameterizedTest
    @ValueSource(strings = {
            "a",
            "A",
            "aB",
            "ab",
            "a-b",
            "a_b",
            "a.b",
            "a_b_c_1_2_3"
    })
    void do_not_generate_constraint_violations_on_valid(String value) {
        testHostInstance.setKey(value);
        final var violations = VALIDATOR.validate(testHostInstance);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "_abc",
            "-abc",
            "a_",
            "a-",
            "1",
            "1_abc"
    })
    void generate_constraint_violations_on_invalid(String value) {
        testHostInstance.setKey(value);
        final var violations = VALIDATOR.validate(testHostInstance);
        assertThat(violations).hasSize(1);
        final var violation = violations.iterator().next();
        assertAll(
                () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("key"),
                () -> assertThat(violation.getMessageTemplate()).startsWith("{" + ValidConfigKey.class.getSimpleName()),
                () -> assertThat(violation.getMessageTemplate()).endsWith("}"),
                () -> assertThat(violation.getMessage()).isNotEqualToIgnoringCase(violation.getMessageTemplate()),
                () -> assertThat(violation.getRootBean()).isEqualTo(testHostInstance)
        );
    }

}
