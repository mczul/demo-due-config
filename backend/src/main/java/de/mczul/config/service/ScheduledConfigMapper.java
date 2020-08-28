package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import de.mczul.config.model.ScheduledConfigPast;
import org.mapstruct.*;

@Mapper
@DecoratedWith(ScheduledConfigMapperDecorator.class)
public interface ScheduledConfigMapper {

    @Mappings({
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "history", ignore = true)
    })
    ScheduledConfigDto fromDomain(ScheduledConfigEntry domain);

    @InheritInverseConfiguration
    ScheduledConfigEntry toDomain(ScheduledConfigDto dto);

    ScheduledConfigPast toPast(ScheduledConfigEntry domain);
}
