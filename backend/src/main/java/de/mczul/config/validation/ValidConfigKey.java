package de.mczul.config.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

import static javax.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Documented
@Pattern(regexp = "[a-z](?:[a-z0-9._-]{0,253}(?<=[a-z0-9]))", flags = CASE_INSENSITIVE)
@ReportAsSingleViolation
public @interface ValidConfigKey {
    String message() default "{ValidConfigKey.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
