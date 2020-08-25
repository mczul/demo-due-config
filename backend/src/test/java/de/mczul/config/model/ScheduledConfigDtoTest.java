package de.mczul.config.model;

import de.mczul.config.service.ScheduledConfigMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.mapstruct.factory.Mappers;

import java.util.stream.Stream;

@DisplayName("ScheduledConfigDto tests")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ScheduledConfigDtoTest extends AbstractScheduledConfigTest {
    private static final ScheduledConfigMapper MAPPER = Mappers.getMapper(ScheduledConfigMapper.class);

    @Override
    public Stream<ScheduledConfigDto> buildValid() {
        return (new ScheduledConfigEntryTest()).buildValid().map(MAPPER::fromDomain).peek((dto) -> dto.setAuthor("john.doe"));
    }

    @Override
    public Stream<ScheduledConfigDto> buildInvalid() {
        return (new ScheduledConfigEntryTest()).buildInvalid().map(MAPPER::fromDomain).peek((dto) -> dto.setAuthor("john.doe"));
    }

    @Override
    protected ScheduledConfig createInstance() {
        return ScheduledConfigDto.builder().build();
    }
}
