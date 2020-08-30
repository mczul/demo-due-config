package de.mczul.config.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import java.util.stream.Stream;

/**
 * Test that now needs to be executed with Spring context due to mapping decorator that adds support for
 * repository features (e.g. comment history);
 * <p>
 * TODO: Support pure unit testing; find a way to inject mocked repository to prevent heavy weight bootstrapping
 */
@DisplayName("ScheduledConfigDto unit tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledConfigDtoTest extends AbstractScheduledConfigTest {

    @Override
    public Stream<ScheduledConfigDto> buildValid() {
        return SampleProvider.buildValidDtos();
    }

    @Override
    public Stream<ScheduledConfigDto> buildInvalid() {
        return SampleProvider.buildInvalidDtos();
    }

    @Override
    protected ScheduledConfig createInstance() {
        return ScheduledConfigDto.builder().build();
    }
}
