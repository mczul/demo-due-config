package de.mczul.config.model;

import de.mczul.config.service.ScheduledConfigMapper;
import de.mczul.config.service.ScheduledConfigRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.stream.Stream;

/**
 * Test that now needs to be executed with Spring context due to mapping decorator that adds support for
 * repository features (e.g. comment history);
 * <p>
 * TODO: Support pure unit testing; find a way to inject mocked repository to prevent heavy weight bootstrapping
 */
@DisplayName("ScheduledConfigDto tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class ScheduledConfigDtoTest extends AbstractScheduledConfigTest {
    // Mock repository to improve performance... yep, that's nothing than a fishy workaround and might be even worse due to dirty context
    @MockBean
    private ScheduledConfigRepository scheduledConfigRepository;
    @Autowired
    private ScheduledConfigMapper mapper;

    @Override
    public Stream<ScheduledConfigDto> buildValid() {
        return (new ScheduledConfigEntryTest()).buildValid().map(mapper::fromDomain).peek((dto) -> dto.setAuthor("john.doe"));
    }

    @Override
    public Stream<ScheduledConfigDto> buildInvalid() {
        return (new ScheduledConfigEntryTest()).buildInvalid().map(mapper::fromDomain).peek((dto) -> dto.setAuthor("john.doe"));
    }

    @Override
    protected ScheduledConfig createInstance() {
        return ScheduledConfigDto.builder().build();
    }
}
