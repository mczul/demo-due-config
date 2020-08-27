package de.mczul.config.service;

import de.mczul.config.model.ScheduledConfigDto;
import de.mczul.config.model.ScheduledConfigEntry;
import org.mapstruct.*;

@Mapper
@DecoratedWith(ScheduledConfigMapperDecorator.class)
public interface ScheduledConfigMapper {

    @Mappings({
            @Mapping(target = "author", ignore = true),
            @Mapping(target = "commentHistory", ignore = true)
    })
    ScheduledConfigDto fromDomain(ScheduledConfigEntry domain);

    @InheritInverseConfiguration
    ScheduledConfigEntry toDomain(ScheduledConfigDto dto);

}
