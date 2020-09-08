package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfig;
import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.model.ScheduledConfigPast;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper
public abstract class ScheduledConfigMapper {
    @Autowired
    protected ScheduledConfigRepository scheduledConfigRepository;

    @Mappings({
            @Mapping(target = "history", ignore = true),
            @Mapping(target = "historyEntry", ignore = true),
    })
    public abstract ScheduledConfigDto toDto(ScheduledConfigEntry entry);

    @InheritInverseConfiguration
    public abstract ScheduledConfigEntry toEntry(ScheduledConfigDto dto);

    public abstract ScheduledConfigPast toPast(ScheduledConfigEntry entry);

    @AfterMapping
    public void enrich(ScheduledConfig source, @MappingTarget ScheduledConfigDto.ScheduledConfigDtoBuilder dto) {
        // history
        List<ScheduledConfigEntry> entries = scheduledConfigRepository.findHistory(source.getKey(), source.getCreated());
        List<ScheduledConfigPast> history = entries.stream().map(this::toPast).collect(Collectors.toUnmodifiableList());
        dto.history(history);
    }

}
