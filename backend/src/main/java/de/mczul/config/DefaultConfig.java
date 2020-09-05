package de.mczul.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile(AppConstants.PROFILES_NOT_TEST)
@EnableScheduling
public class DefaultConfig {

}
