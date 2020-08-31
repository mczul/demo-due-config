package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.model.ScheduledConfigPast;
import org.mapstruct.*;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
@DecoratedWith(ScheduledConfigMapperDecorator.class)
public interface ScheduledConfigMapper {

    @Mappings({
            @Mapping(target = "history", ignore = true),
            @Mapping(target = "historyEntry", ignore = true),
    })
    ScheduledConfigDto toDto(ScheduledConfigEntry domain);

    @InheritInverseConfiguration
    ScheduledConfigEntry toDomain(ScheduledConfigDto dto);

    ScheduledConfigPast toPast(ScheduledConfigEntry domain);
}
