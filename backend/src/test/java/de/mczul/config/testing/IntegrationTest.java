package de.mczul.config.testing;


import de.mczul.config.AppConstants;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@SpringBootTest
@ActiveProfiles
@Tag(TestTags.INTEGRATION_TEST)
public @interface IntegrationTest {
    @AliasFor(annotation = ActiveProfiles.class, attribute = "profiles") String[] activeProfiles() default {AppConstants.PROFILES_TEST};
}
