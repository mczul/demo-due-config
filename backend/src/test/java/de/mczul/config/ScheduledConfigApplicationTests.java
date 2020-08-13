package de.mczul.config;

import de.mczul.config.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("Spring Boot initialization tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@IntegrationTest
class ScheduledConfigApplicationTests {

    @Test
    void assert_that_context_loads() {
    }

}
