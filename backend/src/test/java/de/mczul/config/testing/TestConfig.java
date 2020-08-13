package de.mczul.config.testing;

import de.mczul.config.AppConstants;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles({AppConstants.PROFILES_TEST})
public class TestConfig {
}
