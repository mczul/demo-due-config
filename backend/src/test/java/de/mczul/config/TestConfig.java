package de.mczul.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles({AppConstants.PROFILES_TEST})
public class TestConfig {
}
